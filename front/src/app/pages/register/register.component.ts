import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Observable} from "rxjs";
import { RegisterForm } from '../../models/registerForm';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  // objet ou variable qui va contenir le form
  registerForm!: FormGroup
  registerForm$!: Observable<{
    name: string;
    email: string;
    password: string;
  }>;

  constructor(
      private formBuilder: FormBuilder,
  ) {
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]], // Email requis et valide
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
    this.registerForm$ = this.registerForm.valueChanges;
  }

  // Méthode pour soumettre le formulaire
  onSubmitForm() {
    if (this.registerForm.valid) {
      const { name, email, password } = this.registerForm.value;

      // Appeler un service d'authentification pour inscrire l'utilisateur
      this.authService.register({ name, email, password }).subscribe(
          (response) => {
            // Si l'inscription est réussie, rediriger vers la page de connexion
            this.router.navigate(['/login']);
          },
          (error) => {
            // Si une erreur survient, afficher un message d'erreur
            console.error('Inscription échouée', error);
          }
      );
    } else {
      // Si le formulaire est invalide, afficher une erreur
      console.log('Le formulaire est invalide');
    }
  }
}
