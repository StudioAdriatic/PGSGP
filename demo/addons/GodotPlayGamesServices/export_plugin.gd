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

	# Return the export options offered by the plugin.
	func _get_export_options(platform):
		if platform is EditorExportPlatformAndroid:
			return [
				{
					"option": {
						"name": "play_games_services/app_id",
						"type": TYPE_STRING
					},
					"default_value": ""
				}
			]
		return []

	# Update the manifest element contents to inject required permissions.
	func _get_android_manifest_element_contents(platform, debug):
		if not _supports_platform(platform):
			return ""
		return """
		<uses-permission android:name="android.permission.INTERNET" />
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
		"""

	# Update the application element contents to inject the APP_ID.
	func _get_android_manifest_application_element_contents(platform, debug):
		if not _supports_platform(platform):
			return ""
		var app_id = get_option("play_games_services/app_id")
		if app_id != "":
			return '<meta-data android:name="com.google.android.gms.games.APP_ID" android:value="\\ ' + app_id + '" />'
		return ""

