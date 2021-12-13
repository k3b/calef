# ![](https://github.com/k3b/calef/raw/master/app/src/main/res/drawable/calef.png) CalEF (Calendar Entry Formatter)

Select an entry in Android-Kalender and send/share the entry's content as human readable text.

Usually calendar entries are shared in the human-unreadable technical format "ICS" (or "VCS") that
calendar programs can read.

CalEF receives the technical format and re-sends/re-shares it-s content as human readable text.

Requirements:

<ul>
<li>Android-4.4 (api 19).</li>
<li>Permissions<<ul>
     <li>None</li>
     </ul>
/li>
</ul>

## Requirements:

* Android-4.4 (api 19) or later with camera hardware.
* At least one Camera app must be installed (
  i.e. [Open Camera](https://f-droid.org/en/packages/net.sourceforge.opencamera))
* Required Permissions:
  * CAMERA needed to ask a camera app to take a photo
  * WRITE_EXTERNAL_STORAGE to save the photo to a file

## Technical details

* CalEF plugs into the Android-System-Share/Send-Chooser,
* receives calendarentries in "ICS" or "VCS" format
* translates the entries into human readable text
* and re-sends the generated text to the Android-System-Share/Send-Chooser

## Privacy

No adds, no usertracking, no internet connection, free open source, available on f-droid


-----

## Donations:

If you like this app please consider to donating to https://f-droid.org/donate .

Since android-developping is a hobby (and an education tool) i do not want any money for my apps so
donation should go to projects i benefit from.

