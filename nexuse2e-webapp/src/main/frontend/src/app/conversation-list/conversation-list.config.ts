import {
  Action,
  ActiveFilterList,
  Filter,
  FilterType,
  ListConfig,
} from "../types";

// START FILTERS
const START_DATE_DEFAULT: Date = new Date(new Date().setHours(0, 0, 0, 0));
const END_DATE_DEFAULT: Date = new Date(new Date().setHours(24, 0, 0, 0));

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
    fieldName: "conversationId",
    filterType: FilterType.TEXT,
  },
  choreographyFilter,
  participantFilter,
  {
    fieldName: "status",
    filterType: FilterType.SELECT,
    allowedValues: ["ERROR", "PROCESSING", "IDLE", "COMPLETED"],
  },
];
export const activeFilters: ActiveFilterList = {};
// END FILTERS

// START DISPLAY CONFIG
export const defaultPageSize = 20;
export const mobileConfig: ListConfig[] = [
  {
    fieldName: "conversationId",
    linkUrlRecipe: "../../conversation/$nxConversationId$",
    isHeader: true,
  },
  { fieldName: "choreographyId" },
  { fieldName: "partnerId" },
  { fieldName: "createdDate" },
];

export const desktopConfig: ListConfig[] = [
  {
    fieldName: "conversationId",
    linkUrlRecipe: "../../conversation/$nxConversationId$",
  },
  {
    fieldName: "partnerId",
  },
  {
    fieldName: "choreographyId",
  },
  {
    fieldName: "currentAction",
  },
  {
    fieldName: "createdDate",
  },
  {
    fieldName: "status",
  },
  {
    fieldName: "turnAroundTime",
  },
];
// END DISPLAY CONFIG

// START ACTIONS
export const actions: Action[] = [
  {
    label: "delete",
    icon: "delete",
    actionKey: "/conversations/delete",
  },
];
// END ACTIONS
