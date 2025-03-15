import java.io.*;
import java.security.*;
import java.util.*;

public class Delete_Duplicate {
    HashMap<String, String> hobj = new HashMap<>();
    LinkedList<String> lobj = new LinkedList<>();
    String str = null;

    public boolean add_chksum(String filepath) throws IOException, NoSuchAlgorithmException {
        String result = checksum(filepath);
        if (!hobj.containsKey(result)) {
            hobj.put(result, filepath);
            return true;
        } else {
            str = hobj.get(result);
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

    public void list(String dname) throws Exception {
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
                list(fd.getAbsolutePath());
            } else if (fd.isFile()) {
                if (!add_chksum(fd.getAbsolutePath()) && fd.delete()) {
                    System.out.println("File deleted: " + str + " => " + fd.getName());
                    lobj.add(str + " => " + fd.getName());
                }
            }
        }
    }

    public LinkedList<String> getLlist() {
        return lobj;
    }

    public static void main(String[] args) throws Exception {
        Delete_Duplicate cobj = new Delete_Duplicate();
        cobj.list("/home/admin/Dup_Del");  // Set your folder path here
    }
}

