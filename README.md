# Duplicate File Remover

![Duplicate File Remover](https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/1.png)

A robust Java application designed to identify and remove duplicate files from your file system. This tool helps you reclaim valuable disk space while maintaining data integrity and providing a comprehensive deletion history.

## Features

- **Smart Duplicate Detection**: Uses MD5 checksums to identify duplicate files based on content, not just filename
- **Intelligent File Preservation**: Prioritizes keeping files without "copy" in their names
- **Pre-deletion Review**: Lists all duplicates with a confirmation step before deletion
- **Modern UI**: Clean, intuitive interface with proper visual feedback
- **Deletion History**: Maintains a JSON database of all deleted files
- **System Tray Integration**: Convenient access from your desktop
- **Enhanced Safety**: Multiple checks to prevent accidental data loss

## Screenshots

<div align="center">
  <img src="https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/1.png" alt="Main Screen" style="margin-right: 10px;">
  <img src="https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/2%20(copy).png" alt="Confirmation Dialog">
  <br><br>
  <img src="https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/3%20(copy).png" alt="Results View" style="margin-right: 10px;">
  <img src="https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/4.png" alt="System Tray">
  <img src="https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/5%20(copy).png" alt="Results View" style="margin-right: 10px;">
  <img src="https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/6.png" alt="Results View" style="margin-right: 10px;">
  <img src="https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/7.png" alt="Results View" style="margin-right: 10px;">
  <img src="https://github.com/dipakrasal2009/Duplicate-File-Remover/blob/main/Screenshots/8.png" alt="Results View" style="margin-right: 10px;">

</div>

## Installation

### Prerequisites
- Java 8 or higher
- At least 50MB of free disk space

### Download
1. Download the latest JAR file from the [Releases](https://github.com/dipakrasal2009/Duplicate-File-Remover/releases) page
2. Place the JAR file in a convenient location on your system

### Running the Application
Double-click the JAR file or run from command line:

```bash
java -jar DuplicateFileRemover.jar
```

## Usage

1. **Select Folder**: Click "BROWSE" to select the directory you want to scan
2. **Scan for Duplicates**: Click "SCAN & REMOVE DUPLICATES" to begin
3. **Review**: The application will show a list of duplicate files found
4. **Confirm**: Review the list and confirm deletion
5. **Check Results**: View the list of deleted files and freed space

## How It Works

### Duplicate Detection Algorithm
1. Each file is scanned to generate an MD5 checksum of its content
2. Files with identical checksums are grouped together
3. For each group of duplicates:
   - If one file doesn't have "copy" in the name, it's kept as the original
   - All other duplicates are marked for deletion
   - If all files have "copy" in the name, the first encountered is kept

### Deletion Process
1. Original files are always preserved
2. Only confirmed duplicates are removed
3. A record is kept in `deletion_history.json` with path, name, and timestamp

## Technologies Used

- Java Swing (UI components)
- Java AWT (System tray integration)
- Java Security (MD5 checksum calculation)
- Java I/O and NIO (File operations)
- JSON (Deletion history)
- Multi-threading (Background operations)

## Building from Source

Clone this repository and compile the Java files:

```bash
git clone https://github.com/yourusername/duplicate-file-remover.git
cd duplicate-file-remover
javac front_end.java Delete_Duplicate.java
```

To create a runnable JAR:

```bash
echo -e "Manifest-Version: 1.0\nMain-Class: front_end\n" > manifest.txt
jar cvfm DuplicateFileRemover.jar manifest.txt *.class
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request


---

<div align="center">
  <p>Made with ❤️ by Dipak, Pratik, Sohail</p>
</div>

