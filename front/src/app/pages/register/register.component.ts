import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Observable } from "rxjs";
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import {AuthResponse} from "../../models/authResponse";

import { tap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

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
      private authService: AuthService,
      private router: Router
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

  onSubmitForm():void {
    if (this.registerForm.valid) {
      const { name, email, password } = this.registerForm.value;

      this.authService.register({ name, email, password }).pipe( // appel de la mthd de auth.service
          tap((response: AuthResponse):void => {
            console.log('Inscription réussie', response);
            this.router.navigate(['/']);
          }),
          catchError((error) => {
            console.error('Erreur d\'inscription', error);
            return of(null);  // On retourne un Observable par défaut en cas d'erreur
          })
      ).subscribe();
    } else {
      console.log('Le formulaire est invalide');
    }
  }

}
