@tool
extends EditorPlugin

# A class member to hold the editor export plugin during its lifecycle.
var export_plugin : AndroidExportPlugin

func _enter_tree():
	# Initialization of the plugin goes here.
	export_plugin = AndroidExportPlugin.new()
	add_export_plugin(export_plugin)

func _exit_tree():
	# Clean-up of the plugin goes here.
	remove_export_plugin(export_plugin)
	export_plugin = null

class AndroidExportPlugin extends EditorExportPlugin:
	# Plugin's name.
	var _plugin_name = "GodotPlayGamesServices"

	# Specifies which platform is supported by the plugin.
	func _supports_platform(platform):
		if platform is EditorExportPlatformAndroid:
			return true
		return false

	# Return the paths of the plugin's AAR binaries relative to the 'addons' directory.
	func _get_android_libraries(platform, debug):
		if debug:
			return PackedStringArray(["GodotPlayGamesServices/GodotPlayGamesServices.release.aar"])
		else:
			return PackedStringArray(["GodotPlayGamesServices/GodotPlayGamesServices.release.aar"])

	# Return the plugin's name.
	func _get_name():
		return _plugin_name

	# Return the maven dependencies.
	func _get_android_dependencies(platform, debug):
		if not _supports_platform(platform):
			return PackedStringArray()
		return PackedStringArray([
			# DEPENDENCIES_START
			"com.google.android.gms:play-services-games-v2:21.0.0",
			"com.google.android.gms:play-services-auth:21.2.0",
			"com.google.code.gson:gson:2.13.1",
			"org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3",
			"org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3",
			# DEPENDENCIES_END
		])
