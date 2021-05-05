import { Pipe, PipeTransform } from "@angular/core";
import { DateRange } from "../types";

@Pipe({
  name: "dateRange",
  pure: true,
})
export class DateRangePipe implements PipeTransform {
  transform(value: any, args?: any): DateRange {
    return value;
  }
}
