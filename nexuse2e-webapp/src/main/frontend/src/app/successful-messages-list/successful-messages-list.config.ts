import { ColumnConfig, ColumnType } from "../types";

// START DISPLAY CONFIG
export const SUCCESS_MESS__CHOREOGRAPHY_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.TEXT,
    fieldName: "name",
    label: "choreography",
  },
  { columnType: ColumnType.TEXT, fieldName: "lastInboundTime" },
  { columnType: ColumnType.TEXT, fieldName: "lastOutboundTime" },
];

export const SUCCESS_MESS__PARTNER_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.TEXT,
    fieldName: "name",
    label: "partner",
  },
  { columnType: ColumnType.TEXT, fieldName: "lastInboundTime" },
  { columnType: ColumnType.TEXT, fieldName: "lastOutboundTime" },
];
// END DISPLAY CONFIG
