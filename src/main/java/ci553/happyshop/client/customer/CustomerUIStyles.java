package ci553.happyshop.client.customer;

/**
 * Modern e-commerce UI styling system for Customer Client
 * Follows Material Design principles with e-commerce focus
 */
public class CustomerUIStyles {

    // ==================== COLOR SYSTEM ====================
    public static class Colors {
        // Primary brand colors
        public static final String PRIMARY = "#4A90E2";        // Modern blue (Amazon-like)
        public static final String PRIMARY_DARK = "#357ABD";   // Darker blue
        public static final String PRIMARY_LIGHT = "#E3F2FD";  // Light blue background

        // Secondary colors
        public static final String SUCCESS = "#34C759";        // Green (Success/Add to cart)
        public static final String WARNING = "#FF9500";        // Orange (Warning)
        public static final String ERROR = "#FF3B30";          // Red (Error/Remove)

        // Neutral colors
        public static final String BACKGROUND = "#F8F9FA";     // Page background
        public static final String SURFACE = "#FFFFFF";        // Card/Container background
        public static final String SURFACE_HOVER = "#F5F7FA";  // Hover state
        public static final String BORDER = "#E1E5E9";         // Borders
        public static final String DIVIDER = "#EDF0F2";        // Dividers

        // Text colors
        public static final String TEXT_PRIMARY = "#1A1A1A";   // Primary text
        public static final String TEXT_SECONDARY = "#666666"; // Secondary text
        public static final String TEXT_TERTIARY = "#999999";  // Tertiary text

        // Status colors
        public static final String IN_STOCK = "#34C759";       // In stock green
        public static final String LOW_STOCK = "#FF9500";      // Low stock orange
        public static final String OUT_OF_STOCK = "#FF3B30";   // Out of stock red

    }

    // ==================== TYPOGRAPHY SYSTEM ====================
    public static class Typography {
        // Font families
        public static final String FONT_PRIMARY = "'Segoe UI', -apple-system, BlinkMacSystemFont, 'Roboto', sans-serif";

        // Font sizes (in pixels)
        public static final double HEADLINE1 = 32.0;
        public static final double HEADLINE2 = 28.0;
        public static final double HEADLINE3 = 24.0;
        public static final double HEADLINE4 = 20.0;
        public static final double BODY = 14.0;
        public static final double BODY_SMALL = 12.0;
        public static final double CAPTION = 10.0;

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
        public static final double PILL = 24.0;
        public static final double CIRCLE = 50.0;
    }

    // ==================== SHADOWS ====================
    public static class Shadows {
        public static final String SM = "0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24)";
        public static final String MD = "0 4px 6px rgba(0,0,0,0.1), 0 2px 4px rgba(0,0,0,0.06)";
        public static final String LG = "0 10px 15px rgba(0,0,0,0.1), 0 4px 6px rgba(0,0,0,0.05)";
        public static final String FOCUS = "0 0 0 3px rgba(74, 144, 226, 0.3)";
    }

    // ==================== ANIMATION ====================
    public static class Animation {
        public static final String EASE_IN_OUT = "-fx-transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);";
        public static final String HOVER_TRANSITION = "-fx-transition: all 0.2s ease;";
    }

    // ==================== COMPONENT STYLES ====================
    public static class Components {

        // ===== HEADING STYLES =====
        public static String getHeading1() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.HEADLINE1, Typography.BOLD,
                    Colors.TEXT_PRIMARY
            );
        }

        public static String getHeading2() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.HEADLINE2, Typography.BOLD,
                    Colors.TEXT_PRIMARY
            );
        }

        public static String getHeading3() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.HEADLINE3, Typography.BOLD,
                    Colors.TEXT_PRIMARY
            );
        }


        // ===== TEXT STYLES =====
        public static String getBodyText() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-text-fill: %s; " +
                            "-fx-wrap-text: true;",
                    Typography.FONT_PRIMARY, Typography.BODY,
                    Colors.TEXT_PRIMARY
            );
        }

        public static String getCaptionText() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.BODY_SMALL,
                    Colors.TEXT_SECONDARY
            );
        }


        // ===== BUTTON STYLES =====
        public static String getPrimaryButton() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %f; " +
                            "-fx-padding: %f %f; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s; " +
                            "-fx-border-color: transparent;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.MEDIUM,
                    Colors.PRIMARY, Borders.MD,
                    Spacing.SM, Spacing.LG, Shadows.SM
            );
        }

        public static String getSecondaryButton() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: transparent; " +
                            "-fx-text-fill: %s; " +
                            "-fx-background-radius: %f; " +
                            "-fx-padding: %f %f; " +
                            "-fx-cursor: hand; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: %f;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.MEDIUM,
                    Colors.PRIMARY, Borders.MD,
                    Spacing.SM, Spacing.LG, Colors.PRIMARY, Borders.MD
            );
        }

        public static String getSuccessButton() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %f; " +
                            "-fx-padding: %f %f; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s; " +
                            "-fx-border-color: transparent;",
                    Typography.FONT_PRIMARY, Typography.BODY, Typography.MEDIUM,
                    Colors.SUCCESS, Borders.MD,
                    Spacing.SM, Spacing.LG, Shadows.SM
            );
        }

        public static String getIconButton(String backgroundColor) {
            return String.format(
                    "-fx-font-size: 16px; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %f; " +
                            "-fx-padding: %f; " +
                            "-fx-min-width: 36px; " +
                            "-fx-min-height: 36px; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s;",
                    Typography.MEDIUM, backgroundColor, Borders.CIRCLE,
                    Spacing.XS, Shadows.SM
            );
        }

        // ===== TEXT FIELD STYLES =====
        public static String getTextField() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: %s; " +
                            "-fx-padding: %f %f; " +
                            "-fx-background-radius: %f; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: %f; " +
                            "%s",
                    Typography.FONT_PRIMARY, Typography.BODY,
                    Colors.SURFACE, Colors.TEXT_PRIMARY,
                    Spacing.SM, Spacing.MD, Borders.MD,
                    Colors.BORDER, Borders.MD, Animation.HOVER_TRANSITION
            );
        }

        public static String getTextFieldFocused() {
            return getTextField().replace(
                    "-fx-border-color: " + Colors.BORDER,
                    "-fx-border-color: " + Colors.PRIMARY + "; " +
                            "-fx-effect: " + Shadows.FOCUS
            );
        }

        // ===== CARD STYLES =====
        public static String getCard() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-background-radius: %f; " +
                            "-fx-effect: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: %f; " +
                            "%s",
                    Colors.SURFACE, Borders.LG, Shadows.MD,
                    Colors.BORDER, Borders.LG, Animation.EASE_IN_OUT
            );
        }


        // ===== STATUS BADGES =====
        public static String getStatusBadge(String status) {
            String bgColor;
            String textColor = "white";

            switch (status.toLowerCase()) {
                case "success":
                case "in stock":
                    bgColor = Colors.IN_STOCK;
                    break;
                case "warning":
                case "low stock":
                    bgColor = Colors.LOW_STOCK;
                    break;
                case "error":
                case "out of stock":
                    bgColor = Colors.OUT_OF_STOCK;
                    break;
                default:
                    bgColor = Colors.TEXT_TERTIARY;
            }

            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: %s; " +
                            "-fx-background-radius: %f; " +
                            "-fx-padding: %f %f;",
                    Typography.FONT_PRIMARY, Typography.CAPTION, Typography.MEDIUM,
                    bgColor, textColor, Borders.PILL, Spacing.XS, Spacing.SM
            );
        }

        // ===== PRICE DISPLAY =====
        public static String getPriceDisplay() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %f; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.HEADLINE4, Typography.BOLD,
                    Colors.PRIMARY
            );
        }

        // ===== DIVIDER =====
        public static String getDivider() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-pref-height: 1; " +
                            "-fx-max-height: 1;",
                    Colors.DIVIDER
            );
        }

        // ===== LIST VIEW =====
        public static String getListView() {
            return String.format(
                    "-fx-background-color: transparent; " +
                            "-fx-border-color: transparent; " +
                            "-fx-padding: 0;"
            );
        }

        // ===== SCROLL BAR STYLING =====
        public static String getScrollBar() {
            return String.format(
                    ".scroll-bar:vertical {" +
                            "   -fx-background-color: transparent;" +
                            "}" +
                            ".scroll-bar:vertical .track {" +
                            "   -fx-background-color: transparent;" +
                            "}" +
                            ".scroll-bar:vertical .thumb {" +
                            "   -fx-background-color: %s;" +
                            "   -fx-background-radius: %f;" +
                            "}" +
                            ".scroll-bar:vertical .thumb:hover {" +
                            "   -fx-background-color: %s;" +
                            "}" +
                            ".scroll-bar:horizontal {" +
                            "   -fx-background-color: transparent;" +
                            "}" +
                            ".scroll-bar:horizontal .track {" +
                            "   -fx-background-color: transparent;" +
                            "}" +
                            ".scroll-bar:horizontal .thumb {" +
                            "   -fx-background-color: %s;" +
                            "   -fx-background-radius: %f;" +
                            "}" +
                            ".scroll-bar:horizontal .thumb:hover {" +
                            "   -fx-background-color: %s;" +
                            "}",
                    Colors.BORDER, Borders.PILL, Colors.TEXT_SECONDARY,
                    Colors.BORDER, Borders.PILL, Colors.TEXT_SECONDARY
            );
        }

    }
    public static String getStockStatusStyle(int stock, int ordered) {
        if (stock <= 0) {
            return "out of stock";
        } else if (stock < ordered || stock < 10) {
            return "low stock";
        } else {
            return "in stock";
        }
    }

}