import { ColumnConfig, ColumnType, Separator } from "../types";

// START DISPLAY CONFIG
export const CERTIFICATES_CONFIG: ColumnConfig[] = [
  { columnType: ColumnType.TEXT, fieldName: "configuredFor" },
  { columnType: ColumnType.TEXT, fieldName: "name", label: "certificateName" },
  {
    columnType: ColumnType.TEXT_AND_MORE,
    fieldName: "validity",
    label: "timeRemaining",
    additionalFieldName: "timeUntilExpiry",
    separator: Separator.BRACKETS,
  },
];
// END DISPLAY CONFIG
