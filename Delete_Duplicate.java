import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Delete_Duplicate {
    HashMap<String, String> hobj = new HashMap<>();
    LinkedList<String> lobj = new LinkedList<>();
    // Map to store duplicates before deletion
    HashMap<String, LinkedList<String>> duplicateMap = new HashMap<>();
    // To store deletion records with timestamps
    List<Map<String, String>> deletionRecords = new ArrayList<>();
    String str = null;

    public boolean add_chksum(String filepath) throws IOException, NoSuchAlgorithmException {
        String result = checksum(filepath);
        if (!hobj.containsKey(result)) {
            hobj.put(result, filepath);
            return true;
        } else {
            str = hobj.get(result);
            // Store duplicate files instead of deleting immediately
            if (!duplicateMap.containsKey(result)) {
                duplicateMap.put(result, new LinkedList<>());
            }
            duplicateMap.get(result).add(filepath);
            return false;
        }
    }

    public String checksum(String filepath) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream fis = new FileInputStream(filepath)) {
            byte[] buffer = new byte[1024];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }
        }

        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    // Modified to find duplicates without deleting
    public void findDuplicates(String dname) throws Exception {
        File folder = new File(dname);
        String filepath = folder.getAbsolutePath();
        if (!folder.exists()) {
            System.out.println("Folder does not exist!");
            return;
        }

        String[] arr = folder.list();
        for (String fileName : arr) {
            File fd = new File(filepath + File.separator + fileName);
            if (fd.isDirectory()) {
                System.out.println("FOLDER: " + fd.getName());
                findDuplicates(fd.getAbsolutePath());
            } else if (fd.isFile()) {
                add_chksum(fd.getAbsolutePath());
            }
        }
    }
    
    // Modified deleteDuplicates to record timestamps
    public void deleteDuplicates(List<String> filesToDelete) {
        lobj.clear(); // Clear previous results
        deletionRecords.clear(); // Clear previous records
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        
        for (String filePath : filesToDelete) {
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                lobj.add(file.getAbsolutePath());
                
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
    
    // New method to save deletion records to JSON
    private void saveToJson() {
        if (deletionRecords.isEmpty()) {
            return;
        }
        
        try {
            String jsonFilePath = "deletion_history.json";
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

    // New method to get map of all duplicates
    public HashMap<String, LinkedList<String>> getDuplicateMap() {
        return duplicateMap;
    }

    public LinkedList<String> getLlist() {
        return lobj;
    }

    public static void main(String[] args) throws Exception {
        Delete_Duplicate cobj = new Delete_Duplicate();
        cobj.findDuplicates("/home/admin/Dup_Del");  // Set your folder path here
    }
}

