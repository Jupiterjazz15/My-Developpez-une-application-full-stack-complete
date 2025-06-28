export interface AuthResponse {
  token: string;     // Par exemple, le token JWT
  user: {
    id: number;
    name: string;
    email: string;
  };
}
