package ci553.happyshop.client.warehouse;

import javafx.scene.paint.Color;

/**
 * Design Philosophy:
 * - Professional, clean interface for warehouse staff
 * - Clear visual hierarchy for product management
 * - Distinct color scheme from customer interface
 * - Emphasizes functionality and efficiency
 */
public class WarehouseUIStyles {

    // ==================== COLOR SYSTEM ====================
    public static class Colors {
        // Primary warehouse colors (distinct from customer interface)
        public static final String PRIMARY = "#7C3AED";        // Purple (Professional)
        public static final String PRIMARY_DARK = "#6D28D9";   // Darker purple
        public static final String PRIMARY_LIGHT = "#F5F3FF";  // Light purple background

        // Secondary colors
        public static final String SUCCESS = "#10B981";        // Emerald (Confirmations)
        public static final String SUCCESS_DARK = "#059669";   // Darker green
        public static final String WARNING = "#F59E0B";        // Amber (Stock alerts)
        public static final String ERROR = "#EF4444";          // Red (Delete/Critical)
        public static final String ERROR_DARK = "#DC2626";     // Darker red
        public static final String INFO = "#3B82F6";           // Blue (Information)

        // Neutral colors
        public static final String BACKGROUND = "#F9FAFB";     // Page background
        public static final String SURFACE = "#FFFFFF";        // Card/Container background
        public static final String SURFACE_HOVER = "#F3F4F6";  // Hover state
        public static final String BORDER = "#E5E7EB";         // Borders
        public static final String DIVIDER = "#F3F4F6";        // Dividers

        // Text colors
        public static final String TEXT_PRIMARY = "#111827";   // Primary text
        public static final String TEXT_SECONDARY = "#6B7280"; // Secondary text
        public static final String TEXT_TERTIARY = "#9CA3AF";  // Tertiary text

        // Stock level colors (more vibrant for warehouse)
        public static final String STOCK_CRITICAL = "#DC2626";  // Critical stock red
        public static final String STOCK_LOW = "#F59E0B";       // Low stock amber
        public static final String STOCK_MEDIUM = "#3B82F6";    // Medium stock blue
        public static final String STOCK_HIGH = "#10B981";      // High stock green

        // Form section colors
        public static final String FORM_EDIT = "#E0E7FF";      // Light indigo for edit
        public static final String FORM_NEW = "#FEF3C7";       // Light yellow for new
        public static final String FORM_SEARCH = "#DBEAFE";    // Light blue for search

    }

    // ==================== TYPOGRAPHY SYSTEM ====================
    public static class Typography {
        // Font families
        public static final String FONT_PRIMARY = "'Segoe UI', -apple-system, BlinkMacSystemFont, 'Roboto', sans-serif";

        // Font sizes
        public static final double HEADLINE1 = 32.0;
        public static final double HEADLINE2 = 28.0;
        public static final double HEADLINE3 = 24.0;
        public static final double HEADLINE4 = 20.0;
        public static final double BODY = 14.0;
        public static final double BODY_SMALL = 12.0;

        // Font weights
        public static final String MEDIUM = "500";
        public static final String SEMIBOLD = "600";
        public static final String BOLD = "700";
    }

    // ==================== SPACING SYSTEM (8px grid) ====================
    public static class Spacing {
        public static final double XS = 4.0;
        public static final double SM = 8.0;
        public static final double MD = 16.0;
        public static final double LG = 24.0;
        public static final double XL = 32.0;

    }

    // ==================== BORDER RADIUS ====================
    public static class Borders {
        public static final double MD = 8.0;
        public static final double LG = 12.0;
    }

    // ==================== SHADOWS ====================
    public static class Shadows {
        public static final String SM = "dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2)";
        public static final String MD = "dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 4)";
    }

    // ==================== COMPONENT STYLES ====================
    public static class Components {

        // ===== HEADING STYLES =====

        public static String getHeading2() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.HEADLINE2,
                    Typography.BOLD, Colors.TEXT_PRIMARY
            );
        }

        public static String getHeading3() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.HEADLINE3,
                    Typography.BOLD, Colors.TEXT_PRIMARY
            );
        }


        // ===== TEXT STYLES =====
        public static String getBodyText() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.BODY, Colors.TEXT_PRIMARY
            );
        }

        public static String getLabelStyle() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-padding: %fpx %fpx;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.BOLD,
                    Colors.TEXT_PRIMARY, Colors.PRIMARY_LIGHT,
                    Spacing.XS, Spacing.SM
            );
        }

        // ===== BUTTON STYLES =====
        public static String getSearchButton() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx %fpx; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.BOLD,
                    Colors.PRIMARY, Borders.MD,
                    Spacing.SM, Spacing.LG, Shadows.SM
            );
        }

        public static String getSuccessButton() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx %fpx; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.MEDIUM,
                    Colors.SUCCESS, Borders.MD,
                    Spacing.SM, Spacing.LG, Shadows.SM
            );
        }

        public static String getWarningButton() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx %fpx; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.MEDIUM,
                    Colors.WARNING, Borders.MD,
                    Spacing.SM, Spacing.LG, Shadows.SM
            );
        }

        public static String getErrorButton() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx %fpx; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.MEDIUM,
                    Colors.ERROR, Borders.MD,
                    Spacing.SM, Spacing.LG, Shadows.SM
            );
        }

        public static String getInfoButton() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx %fpx; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.MEDIUM,
                    Colors.INFO, Borders.MD,
                    Spacing.SM, Spacing.LG, Shadows.SM
            );
        }

        public static String getIconButton(String backgroundColor) {
            return String.format(
                    "-fx-font-size: 16px; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx; " +
                            "-fx-min-width: 35px; " +
                            "-fx-pref-width: 35px; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s;",
                    Typography.MEDIUM, backgroundColor, Borders.MD,
                    Spacing.XS, Shadows.SM
            );
        }

        // ===== TEXT FIELD STYLES =====
        public static String getTextField() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: %s; " +
                            "-fx-padding: %fpx %fpx; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: %fpx;",
                    Typography.FONT_PRIMARY, Typography.BODY,
                    Colors.SURFACE, Colors.TEXT_PRIMARY,
                    Spacing.SM, Spacing.MD, Borders.MD,
                    Colors.BORDER, Borders.MD
            );
        }

        public static String getTextFieldReadOnly() {
            return getTextField() + String.format(
                    " -fx-background-color: %s; " +
                            "-fx-opacity: 0.7;",
                    Colors.SURFACE_HOVER
            );
        }

        public static String getTextArea() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-control-inner-background: %s; " +
                            "-fx-text-fill: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: %fpx; " +
                            "-fx-background-radius: %fpx;",
                    Typography.FONT_PRIMARY, Typography.BODY,
                    Colors.SURFACE, Colors.TEXT_PRIMARY,
                    Colors.SURFACE, Colors.BORDER,
                    Borders.MD, Borders.MD
            );
        }

        // ===== FORM SECTION STYLES =====
        public static String getEditFormStyle() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: %fpx; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx; " +
                            "-fx-effect: %s;",
                    Colors.FORM_EDIT, Colors.PRIMARY,
                    Borders.LG, Borders.LG,
                    Spacing.MD, Shadows.MD
            );
        }

        public static String getNewFormStyle() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: %fpx; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx; " +
                            "-fx-effect: %s;",
                    Colors.FORM_NEW, Colors.WARNING,
                    Borders.LG, Borders.LG,
                    Spacing.MD, Shadows.MD
            );
        }

        // ===== COMBO BOX STYLE =====
        public static String getComboBox() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: %fpx; " +
                            "-fx-background-radius: %fpx;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.MEDIUM,
                    Colors.SURFACE, Colors.BORDER,
                    Borders.MD, Borders.MD
            );
        }

        // ===== LIST VIEW STYLE =====
        public static String getListView() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: %fpx; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx;",
                    Colors.SURFACE, Colors.BORDER,
                    Borders.MD, Borders.MD,
                    Typography.FONT_PRIMARY, Typography.BODY
            );
        }

        // ===== IMAGE VIEW STYLES =====
        public static String getImageViewContainer() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: %fpx; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx; " +
                            "-fx-effect: %s;",
                    Colors.PRIMARY_LIGHT, Colors.PRIMARY,
                    Borders.MD, Borders.MD,
                    Spacing.SM, Shadows.SM
            );
        }

        // ===== DIVIDER STYLE =====
        public static String getDivider() {
            return String.format(
                    "-fx-stroke: %s; " +
                            "-fx-stroke-width: 4;",
                    Colors.PRIMARY
            );
        }

        // ===== ROOT CONTAINER STYLE =====
        public static String getRootStyle() {
            return String.format(
                    "-fx-padding: %fpx; " +
                            "-fx-background-color: %s;",
                    Spacing.MD, Colors.BACKGROUND
            );
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Darkens a hex color by percentage
     */
    public static String darkenColor(String hex, double percentage) {
        try {
            Color color = Color.web(hex);
            double factor = 1.0 - percentage;
            return String.format("#%02X%02X%02X",
                    (int)(color.getRed() * 255 * factor),
                    (int)(color.getGreen() * 255 * factor),
                    (int)(color.getBlue() * 255 * factor)
            );
        } catch (Exception e) {
            return hex;
        }
    }

}