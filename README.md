# ![](https://github.com/k3b/calef/raw/master/app/src/main/res/drawable-xxxhdpi/calef.png) CalEF (Calendar Entry Formatter)

Create and send Appointment-Confirmation-Message from Android-Calendar-Entry.

![](https://github.com/k3b/calef/raw/master/app/src/debug/res/drawable/calef_schema.png)

Making an appointment means

* Add date, time and title of the appointment to the Android-Calendar
* In Android-Calendar-Detailview use Send/Share (from menu or toolbar)
* Select "CalEF" as share destination (to convert to human readable text)
* Select the final Send/Share destination (clipboard, sms, mail, ...)

Usually calendar entries are shared in human-unreadable technical format "ICS" (or "VCS") that
calendar programs can read.

CalEF receives the technical format and re-sends/re-shares it-s content as human readable text.

Date/Time is formatted according to language/locale. Examples:

<table>
    <tr><td>Language</td><td>Short</td><td>Long</td></tr>
    <tr><td>العربية 🇸🇦 (Arabic)</td><td>📅السبت ٢٤‏/١٢‏/٢٠٢٢ ١٧:٣٢</td><td>📅السبت ٢٤ ديسمبر ٢٠٢٢ ٥:٣٢:٠٠ م توقيت وسط أوروبا الرسمي</td></tr>
    <tr><td>বাঙালি (Bengali)</td><td>📅শনি ২৪/১২/২২ ১৭:৩২</td><td>📅শনিবার ২৪ ডিসেম্বর ২০২২ ৫:৩২:০০ PM মধ্য ইউরোপীয় মানক সময়</td></tr>
    <tr><td>Deutsch (German) 🇩🇪</td><td>📅Sa. 24.12.22 17:32</td><td>📅Samstag 24. Dezember 2022 17:32:00 Mitteleuropäische Normalzeit</td></tr>
    <tr><td>English US 🇺🇸</td><td>📅Sat 12/24/22 17:32</td><td>📅Saturday December 24 2022 5:32:00 PM Central European Standard Time</td></tr>
    <tr><td>English GB 🇬🇧</td><td>📅Sat 24/12/2022 17:32</td><td>📅Saturday 24 December 2022 17:32:00 Central European Standard Time</td></tr>
    <tr><td>Espanol (Spanish) 🇪🇸</td><td>📅sáb. 24/12/22 17:32</td><td>📅sábado 24 de diciembre de 2022 17:32:00 (hora estándar de Europa central)</td></tr>
    <tr><td>Français (French) 🇫🇷</td><td>📅sam. 24/12/2022 17:32</td><td>📅samedi 24 décembre 2022 17:32:00 heure normale d’Europe centrale</td></tr>
    <tr><td>हिंदी (Hindi)</td><td>📅शनि 24/12/22 17:32</td><td>📅शनिवार 24 दिसंबर 2022 5:32:00 pm मध्य यूरोपीय मानक समय</td></tr>
    <tr><td>Magyar (Hungarian) 🇭🇺</td><td>📅Szo 2022. 12. 24. 17:32</td><td>📅szombat 2022. december 24. 17:32:00 közép-európai téli idő</td></tr>
    <tr><td>Indonesia (Indonesian) 🇮🇩</td><td>📅Sab 24/12/22 17.32</td><td>📅Sabtu 24 Desember 2022 17.32.00 Waktu Standar Eropa Tengah</td></tr>
    <tr><td>日本語 (Japanese) 🇯🇵</td><td>📅土 2022/12/24 17:32</td><td>📅土曜日 2022年12月24日 17時32分00秒 中央ヨーロッパ標準時</td></tr>
    <tr><td>Nederlands (Dutch) 🇳🇱</td><td>📅za 24-12-2022 17:32</td><td>📅zaterdag 24 december 2022 17:32:00 Midden-Europese standaardtijd</td></tr>
    <tr><td>Norsk (Norwegian) 🇳🇴</td><td>📅lør. 24.12.2022 17:32</td><td>📅lørdag 24. desember 2022 17:32:00 sentraleuropeisk normaltid</td></tr>
    <tr><td>Polskie (Polish) 🇵🇱</td><td>📅sob. 24.12.2022 17:32</td><td>📅sobota 24 grudnia 2022 17:32:00 czas środkowoeuropejski standardowy</td></tr>
    <tr><td>Português (Portuguese) 🇵🇹 🇧🇷</td><td>📅sáb 24/12/2022 17:32</td><td>📅sábado 24 de dezembro de 2022 17:32:00 Horário Padrão da Europa Central</td></tr>
    <tr><td>Русский (Russian) 🇷🇺</td><td>📅сб 24.12.2022 17:32</td><td>📅суббота 24 декабря 2022 г. 17:32:00 Центральная Европа, стандартное время</td></tr>
    <tr><td>Türk (Turkish) 🇹🇷</td><td>📅Cmt 24.12.2022 17:32</td><td>📅Cumartesi 24 Aralık 2022 17:32:00 Orta Avrupa Standart Saati</td></tr>
    <tr><td>Українська (Ukrainian) 🇺🇦</td><td>📅сб 24.12.22 17:32</td><td>📅субота 24 грудня 2022 р. 17:32:00 за центральноєвропейським стандартним часом</td></tr>
    <tr><td>Tiếng Việt (Vietnamese) 🇻🇳</td><td>📅Th 7 24/12/2022 17:32</td><td>📅Thứ Bảy 24 tháng 12 2022 17:32:00 Giờ chuẩn Trung Âu</td></tr>
    <tr><td>简体中文 (simplified Chinese)</td><td>📅周六 2022/12/24 17:32</td><td>📅星期六 2022年12月24日 中欧标准时间 下午5:32:00</td></tr>
    <tr><td>繁体中文 (traditional Chinese)</td><td>📅週六 2022/12/24 17:32</td><td>📅星期六 2022年12月24日 下午5:32:00 [中歐標準時間]</td></tr>
</table>

---

![](https://raw.githubusercontent.com/k3b/calef/master/fastlane/metadata/android/en-US/images/phoneScreenshots/CalEF-Settings.png)

Use the settings page to choose language, day, date, time and message format. Under "Example
Day/Date/Time" and "Last used Calendar Event" you can see what the result would be.

---

* Translations via
  crowdin.com: [![Crowdin](https://badges.crowdin.net/calef/localized.svg)](https://crowdin.com/project/calef)
* Download: [<img src="https://f-droid.org/badge/get-it-on.png"
  alt="Get it on F-Droid"
  height="80">](https://f-droid.org/app/de.k3b.android.calef) 
* Download: ![](https://github.com/k3b/calef/raw/master/app/src/debug/res/drawable/qr_code_url_calef_fdroid.png)

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
