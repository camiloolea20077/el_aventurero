import { CreateUsersDto } from "./create-users.dto";

export interface UpdateUsersDto extends CreateUsersDto {
    id: number
}