package ci553.happyshop.systemSetup;

import ci553.happyshop.storageAccess.DatabaseRWFactory;

import java.sql.*;

/**
 * Database Migration utility to add maxStock column to existing ProductTable.
 *
 * This script safely adds the maxStock column if it doesn't exist,
 * and initializes it with current stock values.
 *
 * Usage:
 * Run this class ONCE after upgrading to the new stock level system.
 * It will:
 * 1. Check if maxStock column exists
 * 2. Add the column if missing
 * 3. Initialize maxStock = inStock for all existing products
 *
 * Design Pattern: Idempotent Operation
 * - Safe to run multiple times
 * - Only modifies database if needed
 */
public class DatabaseMigration {

    private static final String dbURL = DatabaseRWFactory.dbURL;

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════");
        System.out.println("  Database Migration: Add maxStock      ");
        System.out.println("════════════════════════════════════════");

        try {
            if (checkIfMaxStockExists()) {
                System.out.println("✅ maxStock column already exists.");
                System.out.println("   No migration needed.");
            } else {
                System.out.println("⚠️  maxStock column not found.");
                System.out.println("   Starting migration...");
                addMaxStockColumn();
                initializeMaxStock();
                verifyMigration();
                System.out.println("✅ Migration completed successfully!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Migration failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("════════════════════════════════════════");
    }

    /**
     * Checks if maxStock column exists in ProductTable.
     */
    private static boolean checkIfMaxStockExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "PRODUCTTABLE", "MAXSTOCK");

            boolean exists = columns.next();
            columns.close();
            return exists;
        }
    }

    /**
     * Adds maxStock column to ProductTable.
     */
    private static void addMaxStockColumn() throws SQLException {
        String alterTableSQL = "ALTER TABLE ProductTable ADD COLUMN maxStock INT DEFAULT 0";

        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement stmt = conn.createStatement()) {

            System.out.println("📝 Adding maxStock column...");
            stmt.executeUpdate(alterTableSQL);
            System.out.println("✅ Column added successfully.");
        }
    }

    /**
     * Initializes maxStock values to match current inStock.
     * This ensures existing products have proper max stock capacity.
     */
    private static void initializeMaxStock() throws SQLException {
        String updateSQL = "UPDATE ProductTable SET maxStock = inStock WHERE maxStock = 0";

        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement stmt = conn.createStatement()) {

            System.out.println("📝 Initializing maxStock values...");
            int rowsUpdated = stmt.executeUpdate(updateSQL);
            System.out.println("✅ Updated " + rowsUpdated + " product(s).");
        }
    }

    /**
     * Verifies migration by displaying sample products.
     */
    private static void verifyMigration() throws SQLException {
        String selectSQL = "SELECT productID, description, inStock, maxStock FROM ProductTable";

        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            System.out.println("\n📊 Verification - Product Stock Status:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-8s %-30s %10s %10s %10s%n",
                    "ID", "Description", "InStock", "MaxStock", "Status");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            int count = 0;
            while (rs.next() && count < 5) {  // Show first 5 products
                String id = rs.getString("productID");
                String desc = rs.getString("description");
                int inStock = rs.getInt("inStock");
                int maxStock = rs.getInt("maxStock");

                double percentage = (double) inStock / maxStock * 100;
                String status;
                if (percentage <= 10) {
                    status = "🚨 LOW";
                } else if (percentage <= 30) {
                    status = "⚠️  MEDIUM";
                } else {
                    status = "✅ HIGH";
                }

                // Truncate description if too long
                String truncDesc = desc.length() > 28 ? desc.substring(0, 28) + ".." : desc;

                System.out.printf("%-8s %-30s %10d %10d %10s%n",
                        id, truncDesc, inStock, maxStock, status);
                count++;
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
}