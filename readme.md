# KotlinConf ICS Generator

This tool downloads the KotlinConf schedule and generates an ICS (iCalendar) file that you can import into your calendar application. Never miss a session again!


[![Add to Google Calendar](https://img.shields.io/badge/Add%20to-Google%20Calendar-4285F4?logo=google-calendar&logoColor=white)](https://calendar.google.com/calendar/u/0/r?cid=webcal://raw.githubusercontent.com/maxandersen/kotlinconfics/refs/heads/main/kotlinconf.ics)

[![Add to Apple Calendar](https://img.shields.io/badge/Add%20to-Apple%20Calendar-000000?logo=apple&logoColor=white)](webcal://raw.githubusercontent.com/maxandersen/kotlinconfics/refs/heads/main/kotlinconf.ics)

## What is this?

This project creates an ICS file containing all sessions from KotlinConf, making it easy to add the conference schedule to your calendar. The generated file includes:
- Session titles and descriptions
- Room locations
- Start and end times
- Speaker information
- Session categories

## Run on your own

1. Install JBang (if you haven't already):
   ```bash
   # macOS
   brew install jbang
   
   # Linux
   curl -Ls https://sh.jbang.dev | bash -s - app setup
   
   # Windows
   scoop install jbang
   ```

2. Run the script:
   ```bash
   jbang KotlinConf2Ics.java
   ```




3. Import the generated `kotlinconf.ics` file into your calendar:
   - **Google Calendar**: 
     1. Go to Settings → Import & Export
     2. Click "Import"
     3. Select the `kotlinconf.ics` file
     4. Choose your calendar and import options
   
   - **Apple Calendar**:
     1. File → Import
     2. Select the `kotlinconf.ics` file
     3. Choose your calendar and import options
   
   - **Outlook**:
     1. File → Open & Export → Import/Export
     2. Choose "Import an iCalendar (.ics) file"
     3. Select the `kotlinconf.ics` file
     4. Choose your calendar and import options

## Features

- 🚀 Automatically fetches the latest schedule from kotlinconf.com
- 📅 Creates a complete conference calendar
- 📝 Includes detailed session information:
  - Session titles and descriptions
  - Room locations
  - Start and end times
  - Speaker details
  - Session categories
- 🔄 Updates automatically when you run the script
- 📱 Compatible with all major calendar applications

## Technical Details

The project uses:
- Quarkus for the REST client
- Biweekly library for ICS file generation
- JBang for easy execution
- Java 17+ compatibility

## Troubleshooting

If you encounter any issues:
1. Make sure you have Java 17 or later installed
2. Verify your internet connection
3. Check that JBang is properly installed
4. Ensure you have write permissions in the current directory

## Contributing

Contributions are welcome! Feel free to:
- Report bugs
- Suggest new features
- Submit pull requests
- Improve documentation

## License

This project is open source and available under the MIT License.

## Author

[Your Name/GitHub Username]

---

Made with ❤️ for the Kotlin community