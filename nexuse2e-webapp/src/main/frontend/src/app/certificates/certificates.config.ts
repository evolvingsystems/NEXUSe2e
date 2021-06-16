import { ColumnConfig, ColumnType } from "../types";

// START DISPLAY CONFIG
export const CERTIFICATES_CONFIG: ColumnConfig[] = [
  { columnType: ColumnType.TEXT, fieldName: "configuredFor" },
  { columnType: ColumnType.TEXT, fieldName: "name", label: "certificateName" },
  {
    columnType: ColumnType.TEXT,
    fieldName: "timeUntilExpiry",
    label: "timeRemaining",
  },
];
// END DISPLAY CONFIG
