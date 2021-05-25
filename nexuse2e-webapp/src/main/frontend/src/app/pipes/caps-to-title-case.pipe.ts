import { Pipe, PipeTransform } from "@angular/core";
import { TitleCasePipe } from "@angular/common";

@Pipe({
  name: "capsToTitleCase",
  pure: true,
})
export class CapsToTitleCasePipe implements PipeTransform {
  transform(value: unknown): unknown {
    if (typeof value === "string") {
      const titleCasePipe = new TitleCasePipe();
      const uppercase = value.toUpperCase();
      if (value === uppercase) {
        return titleCasePipe.transform(value) as string;
      } else {
        return value as string;
      }
    } else {
      return value;
    }
  }
}
