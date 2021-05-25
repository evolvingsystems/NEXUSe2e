import {
  Action,
  ActiveFilterList,
  Filter,
  FilterType,
  ColumnConfig,
  ColumnType,
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

export const CONV_LIST__FILTERS = [
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
export const CONV_LIST__DEFAULT_PAGE_SIZE = 20;

export const CONV_LIST__MOBILE_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.LINK,
    fieldName: "conversationId",
    linkUrlRecipe: "../../conversation/$nxConversationId$",
    isHeader: true,
  },
  { columnType: ColumnType.TEXT, fieldName: "choreographyId" },
  { columnType: ColumnType.TEXT, fieldName: "partnerId" },
  { columnType: ColumnType.TEXT, fieldName: "createdDate" },
];

export const CONV_LIST__DESKTOP_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.LINK,
    fieldName: "conversationId",
    linkUrlRecipe: "../../conversation/$nxConversationId$",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "partnerId",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "choreographyId",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "currentAction",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "createdDate",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "status",
    titleCase: true,
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "turnAroundTime",
  },
];
// END DISPLAY CONFIG

// START ACTIONS
export const CONV_LIST__ACTIONS: Action[] = [
  {
    label: "delete",
    icon: "delete",
    actionKey: "/conversations/delete",
  },
];
// END ACTIONS
