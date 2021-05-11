import { Pipe, PipeTransform } from "@angular/core";
import { DateRange } from "../types";

@Pipe({
  name: "dateRange",
  pure: true,
})
export class DateRangePipe implements PipeTransform {
  transform(value: unknown): DateRange {
    return value as DateRange;
  }
}
