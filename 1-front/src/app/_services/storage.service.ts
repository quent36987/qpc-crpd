import {Inject, Injectable} from '@angular/core';
import {isPlatformBrowser} from '@angular/common';
import {PLATFORM_ID} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {UserDTO} from "./generated-api";
import {TOKEN_TIMEOUT} from "../environments/environment";

@Injectable({
  providedIn: 'root',
})
export class StorageService {

  private _loggedIn = new BehaviorSubject<boolean>(false);

  private _user = new BehaviorSubject<UserDTO | null>(null);

  private _tokenRefreshDate = new BehaviorSubject<number | null>(null);

  public get user$(): Observable<UserDTO | null> {
    return this._user.asObservable();
  }

  public get tokenRefreshDate$(): Observable<number | null> {
    return this._tokenRefreshDate.asObservable();
  }

  constructor(
              @Inject(PLATFORM_ID) private platformId: Object) {
    this.initialize(); // Initialise les données au démarrage
  }

  private initialize() {
    console.log('initialize storage service');
    if (isPlatformBrowser(this.platformId)) {
      const cachedUser = this.getData('qpc-user');
      if (cachedUser) {
        const user: UserDTO = JSON.parse(cachedUser);
        this._user.next(user);
        this._loggedIn.next(true);
      } else {
        this._user.next(null);
        this._loggedIn.next(false);
      }
    }
  }

  // USER
  public saveUser(user: UserDTO) {
    this.removeData('qpc-user');
    this.saveData('qpc-user', JSON.stringify(user));
    this._loggedIn.next(true);
    this._user.next(user);
  }

  public removeUser() {
    console.log('remove user');
    this.removeData('qpc-user');
    this.removeTokenRefreshDate();
    this._loggedIn.next(false);
    this._user.next(null);
  }

  public saveTokenRefreshDate() {
    const date = Date.now() +  TOKEN_TIMEOUT;
    this.saveData('tokenRefreshDate', date.toString());
    this._tokenRefreshDate.next(date);
  }

  public removeTokenRefreshDate() {
    this.removeData('tokenRefreshDate');
    this._tokenRefreshDate.next(null);
  }

  // DATA
  private getData(key: string): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem(key);
    }
    return null;
  }

  private saveData(key: string, value: string) {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(key, value);
    }
  }

  private removeData(key: string) {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(key);
    }
  }

  //LOGIN
  public get loggedIn$(): Observable<boolean> {
    return this._loggedIn.asObservable();
  }

  public isLoggedIn(): boolean {
    return this._loggedIn.value;
  }

  public user(): UserDTO | null {
    return this._user.value;
  }

  public getTokenRefreshDate(): number | null {
    return this._tokenRefreshDate.value;
  }

}
