import { ActiveFilterList, FilterType, ListConfig } from "../types";

// START FILTERS
export const START_DATE_DEFAULT: Date = new Date(
  new Date().setHours(0, 0, 0, 0)
);
export const END_DATE_DEFAULT: Date = new Date(
  new Date().setHours(24, 0, 0, 0)
);

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
export const defaultPageSize = 50;
export const mobileConfig: ListConfig[] = [
  { fieldName: "createdDate" },
  { fieldName: "description" },
];

export const desktopConfig: ListConfig[] = [
  {
    fieldName: "createdDate",
  },
  {
    fieldName: "description",
  },
  {
    fieldName: "className",
  },
  {
    fieldName: "methodName",
  },
];
// END DISPLAY CONFIG
