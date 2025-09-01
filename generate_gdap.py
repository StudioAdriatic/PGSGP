#!/usr/bin/env python3
"""
Script to generate GodotPlayGamesServices.gdap file from build.gradle dependencies.
This ensures the .gdap file is always in sync with the project's dependencies.
"""

import re
import sys
import os
from typing import List, Dict

def extract_dependencies_from_gradle(gradle_file_path: str) -> List[str]:
    """
    Extract implementation dependencies from build.gradle file.
    Returns a list of dependency strings in the format "group:artifact:version"
    """
    dependencies = []
    
    try:
        with open(gradle_file_path, 'r', encoding='utf-8') as file:
            content = file.read()
        
        # Find the dependencies block
        dependencies_match = re.search(r'dependencies\s*\{(.*?)\}', content, re.DOTALL)
        if not dependencies_match:
            print("Error: Could not find dependencies block in build.gradle")
            return dependencies
        
        dependencies_block = dependencies_match.group(1)
        
        # Extract implementation dependencies (excluding test dependencies)
        implementation_pattern = r"implementation\s+['\"]([^'\"]+)['\"]"
        matches = re.findall(implementation_pattern, dependencies_block)
        
        # Filter out test dependencies and project dependencies
        for match in matches:
            if not any(test_keyword in match.lower() for test_keyword in ['test', 'junit', 'mockito', 'espresso', 'robolectric']):
                if not match.startswith('org.jetbrains.kotlin:kotlin-stdlib'):  # Exclude kotlin stdlib as it's not needed in gdap
                    if ':' in match:  # Only include external dependencies with group:artifact:version format
                        dependencies.append(match)
        
        return dependencies
        
    except FileNotFoundError:
        print(f"Error: Could not find file {gradle_file_path}")
        return dependencies
    except Exception as e:
        print(f"Error reading {gradle_file_path}: {e}")
        return dependencies

def generate_gdap_content(dependencies: List[str], library_name: str = "GodotPlayGamesServices") -> str:
    """
    Generate the content for the .gdap file based on the dependencies.
    """
    # Format dependencies as a JSON-like array string
    deps_formatted = ', '.join([f'"{dep}"' for dep in dependencies])
    
    gdap_content = f"""[config]

name="{library_name}"
binary_type="local"
binary="{library_name}.release.aar"

[dependencies]

remote=[{deps_formatted}]
"""
    
    return gdap_content

def main():
    """
    Main function to generate the .gdap file.
    """
    # Default paths
    gradle_file = "app/build.gradle"
    output_file = "GodotPlayGamesServices.gdap"
    
    # Allow command line arguments to override defaults
    if len(sys.argv) > 1:
        gradle_file = sys.argv[1]
    if len(sys.argv) > 2:
        output_file = sys.argv[2]
    
    print(f"Reading dependencies from: {gradle_file}")
    
    # Extract dependencies
    dependencies = extract_dependencies_from_gradle(gradle_file)
    
    if not dependencies:
        print("Warning: No dependencies found!")
        sys.exit(1)
    
    print(f"Found {len(dependencies)} dependencies:")
    for dep in dependencies:
        print(f"  - {dep}")
    
    # Generate .gdap content
    gdap_content = generate_gdap_content(dependencies)
    
    # Write to output file
    try:
        with open(output_file, 'w', encoding='utf-8') as file:
            file.write(gdap_content)
        
        print(f"\nSuccessfully generated: {output_file}")
        print("\nGenerated content:")
        print("-" * 50)
        print(gdap_content)
        
    except Exception as e:
        print(f"Error writing to {output_file}: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
