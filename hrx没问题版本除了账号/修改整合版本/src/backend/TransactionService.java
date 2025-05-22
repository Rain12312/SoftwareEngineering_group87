package backend;

import java.util.*;
import java.io.*;
import backend.LedgerContext;

public class TransactionService {
    public List<Transaction> getAllTransactions() throws IOException {
        String filePath = LedgerContext.getCurrentLedgerFile();
        List<Transaction> list = new ArrayList<>();
        List<String[]> rows = CsvUtil.read(filePath);
        for (String[] row : rows) {
            if (row.length < 4)
                continue;
            try {
                String notes = row.length > 4 ? row[4] : "";
                Transaction t = new Transaction(row[0], row[1], row[2], Double.parseDouble(row[3]), notes);
                list.add(t);
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    public void addTransaction(Transaction t) throws IOException {
        String filePath = LedgerContext.getCurrentLedgerFile();
        List<String[]> rows = CsvUtil.read(filePath);
        rows.add(0, new String[] { t.getDate(), t.getType(), t.getCategory(), String.valueOf(t.getAmount()),
                t.getNotes() });
        CsvUtil.write(filePath, rows);

        // 如果当前账本不是 general，则同步写入 general 账本
        if (!"general".equals(LedgerContext.getCurrentLedger())) {
            String username = backend.UserManager.getCurrentUser();
            if (username != null) {
                String generalFile = username + "_general.csv";
                List<String[]> generalRows = CsvUtil.read(generalFile);
                generalRows.add(0,
                        new String[] { t.getDate(), t.getType(), t.getCategory(), String.valueOf(t.getAmount()),
                                t.getNotes() });
                CsvUtil.write(generalFile, generalRows);
            }
        }
    }
}