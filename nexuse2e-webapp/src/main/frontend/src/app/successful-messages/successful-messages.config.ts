import { ColumnConfig, ColumnType, Separator } from "../types";

// START DISPLAY CONFIG
export const SUCCESS_MESS__CHOREOGRAPHY_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.TEXT_AND_MORE,
    fieldName: "name",
    label: "choreography",
    additionalLinkText: "showConversations",
    additionalLinkUrlRecipe: "../reporting/transaction-reporting/conversations",
    additionalLinkQueryParamsRecipe: {
      startEndDateRange:
        '{"startDate":"$todayMinusTransactionActivityTimeframeInWeeks$"}',
      choreographyId: "$name$",
      status: "Completed",
    },
    separator: Separator.BRACKETS,
  },
  { columnType: ColumnType.TEXT, fieldName: "lastInboundTime" },
  { columnType: ColumnType.TEXT, fieldName: "lastOutboundTime" },
];

export const SUCCESS_MESS__PARTNER_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.TEXT_AND_MORE,
    fieldName: "name",
    label: "partner",
    additionalLinkText: "showConversations",
    additionalLinkUrlRecipe: "../reporting/transaction-reporting/conversations",
    additionalLinkQueryParamsRecipe: {
      startEndDateRange:
        '{"startDate":"$todayMinusTransactionActivityTimeframeInWeeks$"}',
      partnerId: "$name$",
      status: "Completed",
    },
    separator: Separator.BRACKETS,
  },
  { columnType: ColumnType.TEXT, fieldName: "lastInboundTime" },
  { columnType: ColumnType.TEXT, fieldName: "lastOutboundTime" },
];
// END DISPLAY CONFIG
