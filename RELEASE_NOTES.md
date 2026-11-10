## 0.1.1

- Add the verified Google 17.70.11 launcher/recognition mapping; retain 17.73.15.
- Open YouTube Music search through its exported deep-link entry point with the complete encoded query, including when a player task already exists. No playback action is used.
- Detect YouTube Music independently of generic search actions. Preserve existing player preferences and Spotify search routing.
- Leave unknown Google versions untouched, validate hook signatures and log failures without queries.
- Dispatch through an explicit activity without UI instead of relying on a background settings provider, which becomes inaccessible after ColorOS stops the module when it is swiped from recents.
- Read current preferences in the module process; fall back to the original Google search when the player is unavailable or redirection is off.
- APK is debug-signed. Replacing an installation signed by another key requires uninstalling the old build.

## 0.1.0

- Redirect recognized Circle to Search songs to a selected compatible player.
- Add a dynamic-color settings screen and root-assisted Google restart when switching players.
- Tested with Spotify and Google 17.73.15. Other players and Google versions may behave differently.
- APK is signed with the Android debug key.
