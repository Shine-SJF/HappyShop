package ci553.happyshop.client.picker;
/**
 * Design Philosophy:
 * - Clear, action-oriented interface for order pickers
 * - High visibility for order status and details
 * - Distinct color scheme emphasizing fulfillment workflow
 * - Focus on efficiency and quick task completion
 */
public class PickerUIStyles {

    // ==================== COLOR SYSTEM ====================
    public static class Colors {
        // Primary picker colors (Orange/Amber theme for action/fulfillment)
        public static final String PRIMARY = "#F59E0B";        // Amber (Action/Fulfillment)
        public static final String PRIMARY_DARK = "#D97706";   // Darker amber

        // Secondary colors
        public static final String SUCCESS = "#10B981";        // Green (Order collected)
        public static final String SUCCESS_DARK = "#059669";   // Darker green
        public static final String WARNING = "#F59E0B";        // Amber (In progress)
        public static final String ERROR = "#EF4444";          // Red (Issues)

        // Neutral colors
        public static final String SURFACE = "#FFFFFF";        // Card/Container background
        public static final String BORDER = "#E5E7EB";         // Borders


        // Text colors
        public static final String TEXT_PRIMARY = "#111827";   // Primary text
        public static final String TEXT_SECONDARY = "#6B7280"; // Secondary text

        // Order status colors
        public static final String STATUS_PROGRESSING = "#F59E0B";  // Amber - In progress

        // Page background colors
        public static final String PAGE_ORDERMAP = "#FEF3C7";      // Light amber for order list
        public static final String PAGE_DETAIL = "#DBEAFE";        // Light blue for order details

    }

    // ==================== TYPOGRAPHY SYSTEM ====================
    public static class Typography {
        // Font families
        public static final String FONT_PRIMARY = "'Segoe UI', -apple-system, BlinkMacSystemFont, 'Roboto', sans-serif";
        public static final String FONT_MONO = "'SF Mono', 'Monaco', 'Consolas', monospace";

        // Font sizes
        public static final double HEADLINE1 = 32.0;
        public static final double HEADLINE2 = 28.0;
        public static final double HEADLINE3 = 24.0;
        public static final double HEADLINE4 = 20.0;
        public static final double BODY_LARGE = 16.0;
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
        public static final double LG = 12.0;
        public static final double PILL = 24.0;}

    // ==================== SHADOWS ====================
    public static class Shadows {

        public static final String SM = "dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2)";
        public static final String MD = "dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 4)";
        public static final String LG = "dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 6)";
    }

    // ==================== COMPONENT STYLES ====================
    public static class Components {


        public static String getOrderMapPageBackground() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-padding: %fpx;",
                    Colors.PAGE_ORDERMAP, Spacing.LG
            );
        }

        public static String getOrderDetailPageBackground() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-padding: %fpx;",
                    Colors.PAGE_DETAIL, Spacing.LG
            );
        }

        // ===== HEADING STYLES =====
        public static String getHeading1() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.HEADLINE1,
                    Typography.BOLD, Colors.TEXT_PRIMARY
            );
        }

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
                    Typography.SEMIBOLD, Colors.TEXT_PRIMARY
            );
        }


        // ===== TEXT AREA STYLES =====
        public static String getOrderTextArea() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: %s; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: %fpx; " +
                            "-fx-padding: %fpx; " +
                            "-fx-effect: %s;",
                    Typography.FONT_MONO, Typography.BODY,
                    Colors.SURFACE, Colors.TEXT_PRIMARY,
                    Borders.LG, Colors.BORDER, Borders.LG,
                    Spacing.MD, Shadows.SM
            );
        }

        // ===== BUTTON STYLES =====
        public static String getPrimaryButton() {
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
                    Typography.FONT_PRIMARY, Typography.BODY_LARGE, Typography.SEMIBOLD,
                    Colors.PRIMARY, Borders.LG,
                    Spacing.MD, Spacing.XL, Shadows.MD
            );
        }

        public static String getPrimaryButtonHover() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx %fpx; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s; " +
                            "-fx-scale-y: 1.02; " +
                            "-fx-scale-x: 1.02;",
                    Typography.FONT_PRIMARY, Typography.BODY_LARGE, Typography.SEMIBOLD,
                    Colors.PRIMARY_DARK, Borders.LG,
                    Spacing.MD, Spacing.XL, Shadows.LG
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
                    Typography.FONT_PRIMARY, Typography.BODY_LARGE, Typography.SEMIBOLD,
                    Colors.SUCCESS, Borders.LG,
                    Spacing.MD, Spacing.XL, Shadows.MD
            );
        }

        public static String getSuccessButtonHover() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx %fpx; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: %s; " +
                            "-fx-scale-y: 1.02; " +
                            "-fx-scale-x: 1.02;",
                    Typography.FONT_PRIMARY, Typography.BODY_LARGE, Typography.SEMIBOLD,
                    Colors.SUCCESS_DARK, Borders.LG,
                    Spacing.MD, Spacing.XL, Shadows.LG
            );
        }

        // ===== LABEL STYLES =====

        public static String getSubtitleLabel() {
            return String.format(
                    "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-text-fill: %s;",
                    Typography.FONT_PRIMARY, Typography.BODY_LARGE,
                    Typography.MEDIUM, Colors.TEXT_SECONDARY
            );
        }

        // ===== CARD STYLES =====
        public static String getCard() {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx; " +
                            "-fx-effect: %s;",
                    Colors.SURFACE, Borders.LG,
                    Spacing.LG, Shadows.MD
            );
        }

        // ===== STATUS BADGE STYLES =====
        public static String getStatusBadge(String statusColor) {
            return String.format(
                    "-fx-background-color: %s; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-family: %s; " +
                            "-fx-font-size: %fpx; " +
                            "-fx-font-weight: %s; " +
                            "-fx-background-radius: %fpx; " +
                            "-fx-padding: %fpx %fpx;",
                    statusColor,
                    Typography.FONT_PRIMARY, Typography.BODY_SMALL,
                    Typography.SEMIBOLD, Borders.PILL,
                    Spacing.XS, Spacing.MD
            );
        }
    }
}