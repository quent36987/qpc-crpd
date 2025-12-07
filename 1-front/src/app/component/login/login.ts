import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {NotificationService} from "../../_services/notification.service";
import {SpinnerService} from "../../_services/spinner.service";
import {StorageService} from "../../_services/storage.service";
import {AuthApi, LoginRequest, SignupRequest, UserDTO} from "../../_services/generated-api";
import {apiWrapper} from "../../_services/api-wrapper";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class LoginComponent {

  email = '';
  password = '';
  signupEmail = '';
  signupPassword = '';
  signupConfirm = '';

  constructor(
    private authService: AuthApi,
    private storageService: StorageService,
    private spinnerService: SpinnerService,
    private notifService: NotificationService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    if (this.storageService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }

  isLoading = signal(false);
  errorMessage = signal('');
  isSignupMode = signal(false);

  login(): void {
    if (!this.email || !this.password) {
      this.errorMessage.set('Veuillez remplir tous les champs');
      return;
    }

    const loginData: LoginRequest = {
      email: this.email,
      password: this.password
    };

    this.authService.authenticateUser(loginData)
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe({
        next: (data: UserDTO) => {
          this.storageService.saveUser(data);
          this.storageService.saveTokenRefreshDate();

          const returnUrl = this.route.snapshot.queryParams['redirectUrl'] || '/';
          this.router.navigateByUrl(returnUrl);
        },
        error: () => {
          this.errorMessage.set('Email ou mot de passe incorrect');
        }
      });
  }

  async onSignup() {
    if (!this.signupEmail || !this.signupPassword || !this.signupConfirm) {
      this.errorMessage.set('Veuillez remplir tous les champs');
      return;
    }

    if (this.signupPassword !== this.signupConfirm) {
      this.errorMessage.set('Les mots de passe ne correspondent pas');
      return;
    }

    if (this.signupPassword.length < 6) {
      this.errorMessage.set('Le mot de passe doit contenir au moins 6 caractères');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const registerData: SignupRequest = {
      prenom: '', //this.prenom,
      nom: '', //this.nom,
      email: this.email,
      password: this.password,
      tel: "0",
    };

    this.authService.registerUser(registerData)
      .pipe(apiWrapper(this.spinnerService, this.notifService, 'Erreur lors de l\'inscription', 'Inscription réussie'))
      .subscribe({
        next: (user : UserDTO) => {
          this.storageService.saveUser(user);
          this.storageService.saveTokenRefreshDate();

          setTimeout(() => {
            this.router.navigate(['/']);
          }, 200);
        }
      });
  }

  toggleMode() {
    this.isSignupMode.update(val => !val);
    this.errorMessage.set('');
  }
}
