import {
  ActiveFilterList,
  ColumnConfig,
  ColumnType,
  FilterType,
} from "../types";

// START FILTERS
export const START_DATE_DEFAULT: Date = new Date(
  new Date().setHours(0, 0, 0, 0)
);
export const END_DATE_DEFAULT: Date = new Date(
  new Date().setHours(24, 0, 0, 0)
);

export const ENGINE_LOG__FILTERS = [
  {
    fieldName: "startEndDateRange",
    filterType: FilterType.DATE_TIME_RANGE,
    defaultValue: {
      startDate: START_DATE_DEFAULT,
      endDate: END_DATE_DEFAULT,
    },
  },
  {
    fieldName: "severity",
    filterType: FilterType.SELECT,
    allowedValues: ["ERROR", "WARN", "INFO", "DEBUG", "TRACE"],
  },
  {
    fieldName: "messageText",
    filterType: FilterType.TEXT,
  },
];
export const activeFilters: ActiveFilterList = {};
// END FILTERS

// START DISPLAY CONFIG
export const ENGINE_LOG__DEFAULT_PAGE_SIZE = 50;

export const ENGINE_LOG__MOBILE_CONFIG: ColumnConfig[] = [
  { columnType: ColumnType.TEXT, fieldName: "createdDate" },
  { columnType: ColumnType.TEXT, fieldName: "description" },
];

export const ENGINE_LOG__DESKTOP_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.TEXT,
    fieldName: "createdDate",
  },
  {
    columnType: ColumnType.LONG_TEXT,
    fieldName: "description",
    showCopyIcon: true,
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "className",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "methodName",
  },
];
// END DISPLAY CONFIG
