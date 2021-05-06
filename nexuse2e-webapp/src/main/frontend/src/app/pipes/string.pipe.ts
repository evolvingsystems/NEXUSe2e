import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: "string",
  pure: true,
})
export class StringPipe implements PipeTransform {
  transform(value: unknown): string {
    if (value && typeof value != "string") {
      console.error("Object", value, "is not of type string.");
    }
    return value as string;
  }
}
