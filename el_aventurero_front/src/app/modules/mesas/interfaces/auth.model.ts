export interface LoginDto {
    email: string;
    password: string;
}

export interface AuthResponse {
    user: {
        id: number;
        name: string;
        email: string;
        role: string;
        permisos: string[];
    };
    token: string;
}
