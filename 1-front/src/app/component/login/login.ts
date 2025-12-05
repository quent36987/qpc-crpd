import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  signupEmail = '';
  signupPassword = '';
  signupConfirm = '';

  isLoading = signal(false);
  errorMessage = signal('');
  isSignupMode = signal(false);

  async onLogin() {
    if (!this.email || !this.password) {
      this.errorMessage.set('Veuillez remplir tous les champs');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    try {
      const { data, error } = await this.authService.signIn(this.email, this.password);

      if (error) {
        this.errorMessage.set('Email ou mot de passe incorrect');
      } else {
        this.router.navigate(['/']);
      }
    } catch (err) {
      this.errorMessage.set('Une erreur est survenue');
    } finally {
      this.isLoading.set(false);
    }
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

    try {
      const { data, error } = await this.authService.signUp(this.signupEmail, this.signupPassword);

      if (error) {
        this.errorMessage.set(error.message);
      } else {
        this.router.navigate(['/']);
      }
    } catch (err) {
      this.errorMessage.set('Une erreur est survenue');
    } finally {
      this.isLoading.set(false);
    }
  }

  toggleMode() {
    this.isSignupMode.update(val => !val);
    this.errorMessage.set('');
  }
}
