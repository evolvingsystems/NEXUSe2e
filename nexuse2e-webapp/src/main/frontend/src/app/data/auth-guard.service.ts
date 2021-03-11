import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, } from "@angular/router";
import { Injectable } from "@angular/core";
import { DataService } from "./data.service";

@Injectable({
  providedIn: "root",
})
export class AuthGuardService implements CanActivate {
  constructor(private router: Router, private dataService: DataService) {}

  async canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    try {
      // This response should not be cached because otherwise,
      // the user would not be logged out if their session expires
      await this.dataService.get("/logged-in");
    } catch {
      await this.router.navigate(["/login"], {
        queryParams: { returnUrl: state.url },
      });
      return false;
    }
    return true;
  }
}
