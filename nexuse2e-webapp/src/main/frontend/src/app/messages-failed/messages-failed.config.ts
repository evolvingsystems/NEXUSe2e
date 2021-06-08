// START DISPLAY CONFIG
import { ColumnConfig, ColumnType, Separator } from "../types";

export const FAILED_MESSAGES_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.LINK_AND_MORE,
    fieldName: "messageId",
    linkUrlRecipe: "../reporting/messages/$nxMessageId$",
    additionalLinkText: "showMessages",
    additionalLinkUrlRecipe: "../reporting/transaction-reporting/messages",
    additionalLinkQueryParamsRecipe: {
      startEndDateRange: '{"startDate":"$todayMinusDashboardTimeFrameInDays$"}',
      status: "Failed",
    },
    separator: Separator.BRACKETS,
  },
  { columnType: ColumnType.TEXT, fieldName: "createdDate" },
  { columnType: ColumnType.TEXT, fieldName: "partnerId", label: "partner" },
  {
    columnType: ColumnType.TEXT,
    fieldName: "choreographyId",
    label: "choreography",
  },
  {
    columnType: ColumnType.LINK,
    fieldName: "conversationId",
    linkUrlRecipe: "../reporting/conversation/$nxConversationId$",
  },
  {
    columnType: ColumnType.ACTION_BUTTON,
    fieldName: "requeue",
    actionButton: {
      label: "requeue",
      icon: "refresh",
      actionKey: "/message/requeue",
    },
  },
];

export const CARD_LINK_CONFIG = {
  linkUrl: "../reporting/transaction-reporting/messages",
  linkParamsRecipe: {
    startEndDateRange: '{"startDate":"$todayMinusDashboardTimeFrameInDays$"}',
    status: "Failed",
  },
};
// END DISPLAY CONFIG
