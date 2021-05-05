import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: "string",
  pure: true,
})
export class StringPipe implements PipeTransform {
  transform(value: any, args?: any): string {
    return value;
  }
}
