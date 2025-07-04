import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  loginForm$!: Observable<{
    usernameOrEmail: string;
    password: string;
  }>;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      usernameOrEmail: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });

    this.loginForm$ = this.loginForm.valueChanges;
  }

  // Méthode pour envoyer les données
  onSubmitForm(): void {
    if (this.loginForm.valid) {
      const { usernameOrEmail, password } = this.loginForm.value;

      // Le service d'authentification
      this.authService.login({ usernameOrEmail, password }).subscribe(
        (response) => {
          console.log('Connexion réussie', response);
          // Redirection vers la page d'accueil ou un autre chemin
          this.router.navigate(['/']);
        },
        (error) => {
          console.error('Erreur lors de la connexion', error);
        }
      );
    } else {
      console.log('Le formulaire est invalide');
    }
  }
}
