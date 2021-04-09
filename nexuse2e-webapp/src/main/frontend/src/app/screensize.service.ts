import { Injectable, RendererFactory2 } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class ScreensizeService {
  innerWidth = window.innerWidth;

  constructor(private rendererFactory2: RendererFactory2) {
    const renderer = this.rendererFactory2.createRenderer(null, null);
    renderer.listen("window", "resize", () => {
      this.innerWidth = window.innerWidth;
    });
  }

  isMobile() {
    return this.innerWidth < 980;
  }
}
