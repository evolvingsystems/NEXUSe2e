import { Action, ActiveFilterList, Filter, FilterType, ListConfig } from "../types";

// START FILTERS
const START_DATE_DEFAULT: Date = new Date(
  new Date().setHours(0, 0, 0, 0)
);
const END_DATE_DEFAULT: Date = new Date(
  new Date().setHours(24, 0, 0, 0)
);

export const participantFilter: Filter = {
  fieldName: "partnerId",
  filterType: FilterType.TEXT,
};
export const choreographyFilter: Filter = {
  fieldName: "choreographyId",
  filterType: FilterType.TEXT,
};

export const filters = [
  {
    fieldName: "startEndDateRange",
    filterType: FilterType.DATE_TIME_RANGE,
    defaultValue: {
      startDate: START_DATE_DEFAULT,
      endDate: END_DATE_DEFAULT,
    },
  },
  {
    fieldName: "messageId",
    filterType: FilterType.TEXT,
  },
  {
    fieldName: "conversationId",
    filterType: FilterType.TEXT,
  },
  choreographyFilter,
  participantFilter,
  {
    fieldName: "status",
    filterType: FilterType.SELECT,
    allowedValues: [
      "FAILED",
      "SENT",
      "UNKNOWN",
      "RETRYING",
      "QUEUED",
      "STOPPED",
    ],
  },
  {
    fieldName: "type",
    filterType: FilterType.SELECT,
    allowedValues: ["NORMAL", "ACKNOWLEDGEMENT", "ERROR"],
    defaultValue: "NORMAL",
  },
];
export const activeFilters: ActiveFilterList = {};
// END FILTERS

// START DISPLAY CONFIG
export const defaultPageSize = 20;

export const desktopConfig: ListConfig[] = [
  {
    fieldName: "messageId",
    linkUrlRecipe: "$nxMessageId$",
  },
  {
    fieldName: "conversationId",
    linkUrlRecipe: "../conversations/$nxConversationId$",
  },
  { fieldName: "partnerId" },
  { fieldName: "status" },
  { fieldName: "backendStatus" },
  { fieldName: "typeName", label: "messageType" },
  {
    fieldName: "choreographyId",
    additionalFieldName: "actionId",
    label: "step",
  },
  { fieldName: "createdDate" },
  { fieldName: "turnAroundTime" },
];

export const mobileConfig: ListConfig[] = [
  {
    fieldName: "messageId",
    linkUrlRecipe: "$nxMessageId$",
    isHeader: true,
  },
  {
    fieldName: "conversationId",
    linkUrlRecipe: "../conversations/$nxConversationId$",
  },
  { fieldName: "partnerId" },
  { fieldName: "typeName", label: "messageType" },
  {
    fieldName: "choreographyId",
    additionalFieldName: "actionId",
    label: "step",
  },
  { fieldName: "createdDate" },
];
// END DISPLAY CONFIG

// START ACTIONS
export const actions: Action[] = [
  {
    label: "requeue",
    icon: "refresh",
    actionKey: "/messages/requeue",
  },
  {
    label: "stop",
    icon: "stop",
    actionKey: "/messages/stop",
  },
];
// END ACTIONS
