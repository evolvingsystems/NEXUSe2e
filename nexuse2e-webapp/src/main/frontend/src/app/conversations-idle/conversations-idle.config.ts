import { ColumnConfig, ColumnType, Separator } from "../types";

// START DISPLAY CONFIG
export const IDLE_CONV_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.TEXT,
    fieldName: "modifiedDate",
    label: "idleSince",
  },
  {
    columnType: ColumnType.LINK_AND_MORE,
    fieldName: "conversationId",
    linkUrlRecipe: "../reporting/conversation/$nxConversationId$",
    additionalLinkText: "showConversations",
    additionalLinkUrlRecipe: "../reporting/transaction-reporting/conversations",
    additionalLinkQueryParamsRecipe: {
      startEndDateRange: '{"startDate":"$todayMinusDashboardTimeFrameInDays$"}',
      status: "Idle",
    },
    separator: Separator.BRACKETS,
  },

  {
    columnType: ColumnType.TEXT,
    fieldName: "partnerId",
    label: "partner",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "choreographyId",
    label: "choreography",
  },
];

export const CARD_LINK_CONFIG = {
  linkUrl: "../reporting/transaction-reporting/conversations",
  linkParamsRecipe: {
    startEndDateRange: '{"startDate":"$todayMinusDashboardTimeFrameInDays$"}',
    status: "Idle",
  },
};
// END DISPLAY CONFIG
