package backend;

public class Transaction {
    private String date;
    private String type; // PAY/INC/TRAN
    private String category;
    private double amount;
    private String username;
    private String notes;

    public Transaction(String date, String type, String category, double amount) {
        this(date, type, category, amount, "");
    }

    public Transaction(String date, String type, String category, double amount, String notes) {
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.username = UserManager.getCurrentUser();
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return date + "," + type + "," + category + "," + amount + "," + notes;
    }
}