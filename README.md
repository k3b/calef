# ![](https://github.com/k3b/calef/raw/master/app/src/main/res/drawable-xxxhdpi/calef.png) CalEF (Calendar Entry Formatter)

Create and send Appointment-Confirmation-Message from Android-Calendar-Entry.

![](https://github.com/k3b/calef/raw/master/fastlane/metadata/android/en-US/images/phoneScreenshots/CalEF-Schema.png)

Making an appointment means

* Add date, time and title of the appointment to the Android-Calendar
* In Android-Calendar-Detailview use Send/Share (from menu or toolbar)
* Select "CalEF" as share destination (to convert to human readable text)
* Select the final Send/Share destination (clipboard, sms, mail, ...)

Usually calendar entries are shared in human-unreadable technical format "ICS" (or "VCS") that
calendar programs can read.

CalEF receives the technical format and re-sends/re-shares it-s content as human readable text.

Date/Time is formatted according to current language/locale.

* Translations via
  crowdin.com: [![Crowdin](https://badges.crowdin.net/calef/localized.svg)](https://crowdin.com/project/calef)
* Download: [<img src="https://f-droid.org/badge/get-it-on.png"
  alt="Get it on F-Droid"
  height="80">](https://f-droid.org/app/de.k3b.android.calef)

## Requirements:

* Android-4.0 (api 14) or later
* Required Permissions:
  * none

## Compatible android apps

* [Etar-Calendar](https://github.com/Etar-Group/Etar-Calendar/)  version 1.0.29 or later (January
  2022)
* [Simple-Calendar](https://github.com/SimpleMobileTools/Simple-Calendar/) version 4.0.0 or later (
  May 2018)
* Buildin Android-Calender (Tested with android-10 on LG device)
* Backup [Calendar Import-Export](https://f-droid.org/packages/org.sufficientlysecure.ical) version
  2.4 or later (November 2016)

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
