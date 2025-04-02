import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Delete_Duplicate {
    // Map to store file checksum -> original file path
    private HashMap<String, String> originalFiles = new HashMap<>();
    
    // Map to store checksum -> list of duplicate files
    private HashMap<String, LinkedList<String>> duplicateMap = new HashMap<>();
    
    // List to store deleted file paths
    private LinkedList<String> deletedFiles = new LinkedList<>();
    
    // To store deletion records with timestamps
    private List<Map<String, String>> deletionRecords = new ArrayList<>();

    // Find and track duplicates without deleting
    public void list(String path) throws Exception {
        // Clear previous data
        originalFiles.clear();
        duplicateMap.clear();
        deletedFiles.clear();
        
        // First pass: collect all files with their checksums
        Map<String, List<String>> allFilesByChecksum = new HashMap<>();
        collectFiles(new File(path), allFilesByChecksum);
        
        // Second pass: determine which files to keep vs delete
        processFilesForDeletion(allFilesByChecksum);
    }
    
    // Collect all files and group them by checksum
    private void collectFiles(File directory, Map<String, List<String>> allFilesByChecksum) throws Exception {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                collectFiles(file, allFilesByChecksum);
            } else if (file.isFile()) {
                String filePath = file.getAbsolutePath();
                String fileChecksum = calculateChecksum(file);
                
                if (!allFilesByChecksum.containsKey(fileChecksum)) {
                    allFilesByChecksum.put(fileChecksum, new ArrayList<>());
                }
                allFilesByChecksum.get(fileChecksum).add(filePath);
            }
        }
    }
    
    // Determine which files to keep vs which to delete
    private void processFilesForDeletion(Map<String, List<String>> allFilesByChecksum) {
        for (Map.Entry<String, List<String>> entry : allFilesByChecksum.entrySet()) {
            String checksum = entry.getKey();
            List<String> files = entry.getValue();
            
            if (files.size() <= 1) {
                // No duplicates for this file
                originalFiles.put(checksum, files.get(0));
                continue;
            }
            
            // Find the best file to keep (preferably one without "copy" in name)
            String fileToKeep = selectFileToKeep(files);
            originalFiles.put(checksum, fileToKeep);
            
            // Add all other files as duplicates
            duplicateMap.put(checksum, new LinkedList<>());
            for (String filePath : files) {
                if (!filePath.equals(fileToKeep)) {
                    duplicateMap.get(checksum).add(filePath);
                }
            }
        }
    }
    
    // Select which file to keep - prefer files without "copy" in name
    private String selectFileToKeep(List<String> files) {
        // First, try to find a file without "copy" in the name (case insensitive)
        for (String filePath : files) {
            File file = new File(filePath);
            String fileName = file.getName().toLowerCase();
            
            if (!fileName.contains("copy")) {
                return filePath;
            }
        }
        
        // If all files have "copy" in the name, keep the first one
        return files.get(0);
    }
    
    // Calculate file checksum
    private String calculateChecksum(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }
        
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    // Get list of all duplicate files only
    public ArrayList<String> getDuplicatesList() {
        ArrayList<String> allDuplicates = new ArrayList<>();
        for (LinkedList<String> duplicates : duplicateMap.values()) {
            allDuplicates.addAll(duplicates);
        }
        return allDuplicates;
    }
    
    // Get original file for a specific checksum
    public String getOriginalFile(String checksum) {
        return originalFiles.get(checksum);
    }
    
    // Get map of all original files
    public HashMap<String, String> getOriginalFiles() {
        return originalFiles;
    }
    
    // Delete specific duplicate files
    public void deleteDuplicates(List<String> filesToDelete) {
        deletedFiles.clear();
        deletionRecords.clear();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        
        for (String filePath : filesToDelete) {
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                deletedFiles.add(filePath);
                
                // Create record for this deleted file
                Map<String, String> record = new HashMap<>();
                record.put("path", filePath);
                record.put("name", file.getName());
                record.put("deleted_at", timestamp);
                deletionRecords.add(record);
            }
        }
        
        // Save to JSON file
        saveToJson();
    }
    
    // Save deletion records to JSON
    private void saveToJson() {
        if (deletionRecords.isEmpty()) {
            return;
        }
        
        try {
            String jsonFilePath = "deletion_history1.json";
            File jsonFile = new File(jsonFilePath);
            
            // Read existing data if file exists
            List<Map<String, String>> allRecords = new ArrayList<>();
            if (jsonFile.exists()) {
                try {
                    String content = new String(Files.readAllBytes(jsonFile.toPath()));
                    // Very basic parsing to extract records from existing JSON
                    if (content.contains("[") && content.contains("]")) {
                        content = content.substring(content.indexOf('[') + 1, content.lastIndexOf(']')).trim();
                        if (!content.isEmpty()) {
                            // Split records on closing and opening brackets
                            String[] records = content.split("\\},\\s*\\{");
                            for (String record : records) {
                                // Clean up the record string
                                record = record.replace("{", "").replace("}", "");
                                Map<String, String> recordMap = new HashMap<>();
                                
                                // Split into key-value pairs
                                String[] pairs = record.split(",");
                                for (String pair : pairs) {
                                    String[] keyValue = pair.split(":");
                                    if (keyValue.length == 2) {
                                        // Remove quotes and trim
                                        String key = keyValue[0].trim().replace("\"", "");
                                        String value = keyValue[1].trim().replace("\"", "");
                                        recordMap.put(key, value);
                                    }
                                }
                                
                                if (!recordMap.isEmpty()) {
                                    allRecords.add(recordMap);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error reading existing JSON file: " + e.getMessage());
                    // If there's an error, we'll just start fresh
                }
            }
            
            // Add new records to the existing ones
            allRecords.addAll(deletionRecords);
            
            // Write JSON
            StringBuilder json = new StringBuilder("[\n");
            for (int i = 0; i < allRecords.size(); i++) {
                Map<String, String> record = allRecords.get(i);
                json.append("  {\n");
                
                // Add each property
                int j = 0;
                for (Map.Entry<String, String> entry : record.entrySet()) {
                    json.append("    \"").append(entry.getKey()).append("\": \"")
                        .append(entry.getValue().replace("\"", "\\\"")).append("\"");
                    
                    if (j < record.size() - 1) {
                        json.append(",");
                    }
                    json.append("\n");
                    j++;
                }
                
                json.append("  }");
                if (i < allRecords.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("]\n");
            
            // Write to file
            try (FileWriter writer = new FileWriter(jsonFilePath)) {
                writer.write(json.toString());
            }
            
            System.out.println("Deletion history saved to " + jsonFilePath);
        } catch (Exception e) {
            System.err.println("Error saving deletion history: " + e.getMessage());
        }
    }
    
    // Get map of all duplicates found
    public HashMap<String, LinkedList<String>> getDuplicateMap() {
        return duplicateMap;
    }
    
    // Get list of deleted files
    public LinkedList<String> getLlist() {
        return deletedFiles;
    }

    public static void main(String[] args) throws Exception {
        Delete_Duplicate cobj = new Delete_Duplicate();
        cobj.list("/home/admin/Dup_Del");  // Set your folder path here
    }
}

