import { Pipe, PipeTransform } from "@angular/core";
import { DateRange } from "../types";

@Pipe({
  name: "dateRange",
  pure: true,
})
export class DateRangePipe implements PipeTransform {
  transform(value: unknown): DateRange {
    if (value && (value as DateRange).startDate == undefined) {
      console.error("Object", value, "is not of type DateRange.");
    }
    return value as DateRange;
  }
}
