import java.io.*;
import java.security.*;
import java.util.*;

public class Delete_Duplicate {
    HashMap<String, String> hobj = new HashMap<>();
    LinkedList<String> lobj = new LinkedList<>();
    // Map to store duplicates before deletion
    HashMap<String, LinkedList<String>> duplicateMap = new HashMap<>();
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
    
    // New method to delete confirmed duplicates
    public void deleteDuplicates(List<String> filesToDelete) {
        lobj.clear(); // Clear previous results
        for (String filePath : filesToDelete) {
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                lobj.add(file.getAbsolutePath());
            }
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

