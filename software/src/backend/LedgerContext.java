package backend;

import java.util.*;
import java.io.*;
import java.nio.file.*;

public class LedgerContext {
    private static String currentLedger = "daily"; // 默认账本
    private static String currentLedgerType = "personal"; // or "general"
    private static final String LEDGER_LIST_FILE = "ledger_list.csv";

    public static String getCurrentLedger() { return currentLedger; }
    public static String getCurrentLedgerType() { return currentLedgerType; }
    public static void setCurrentLedger(String ledger, String type) {
        currentLedger = ledger;
        currentLedgerType = type;
    }
    public static String getCurrentLedgerFile() { 
        String username = UserManager.getCurrentUser();
        if (username == null) return null;
        return username + "_" + currentLedger + ".csv"; 
    }

    // 获取所有账本
    public static List<String[]> getAllLedgers() {
        List<String[]> ledgers = new ArrayList<>();
        try {
            File file = new File(LEDGER_LIST_FILE);
            if (!file.exists()) {
                // 默认账本
                ledgers.add(new String[]{"general", "general"});
                ledgers.add(new String[]{"daily", "personal"});
                ledgers.add(new String[]{"love", "personal"});
                saveAllLedgers(ledgers);
            } else {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    ledgers.add(line.split(","));
                }
                br.close();
            }
        } catch (Exception e) {}
        return ledgers;
    }

    // 添加新账本
    public static void addLedger(String name, String type) {
        List<String[]> ledgers = getAllLedgers();
        for (String[] l : ledgers) {
            if (l[0].equals(name)) return; // 不重复添加
        }
        ledgers.add(new String[]{name, type});
        saveAllLedgers(ledgers);
        
        // 为当前用户创建新的账本文件
        String username = UserManager.getCurrentUser();
        if (username != null) {
            try {
                String ledgerFile = username + "_" + name + ".csv";
                if (!Files.exists(Paths.get(ledgerFile))) {
                    Files.createFile(Paths.get(ledgerFile));
                    Files.write(Paths.get(ledgerFile), "Date,Type,Category,Amount\n".getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 删除账本
    public static void deleteLedger(String name) {
        List<String[]> ledgers = getAllLedgers();
        ledgers.removeIf(l -> l[0].equals(name));
        saveAllLedgers(ledgers);
        
        // 删除当前用户的账本文件
        String username = UserManager.getCurrentUser();
        if (username != null) {
            File f = new File(username + "_" + name + ".csv");
            if (f.exists()) f.delete();
        }
    }

    // 重命名账本
    public static void renameLedger(String oldName, String newName) {
        List<String[]> ledgers = getAllLedgers();
        for (String[] l : ledgers) {
            if (l[0].equals(oldName)) l[0] = newName;
        }
        saveAllLedgers(ledgers);
        
        // 重命名当前用户的账本文件
        String username = UserManager.getCurrentUser();
        if (username != null) {
            File oldFile = new File(username + "_" + oldName + ".csv");
            File newFile = new File(username + "_" + newName + ".csv");
            if (oldFile.exists()) oldFile.renameTo(newFile);
        }
    }

    // 保存账本列表
    private static void saveAllLedgers(List<String[]> ledgers) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LEDGER_LIST_FILE))) {
            for (String[] row : ledgers) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (Exception e) {}
    }
} 