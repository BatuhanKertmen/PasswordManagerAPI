using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PasswordManager.Migrations
{
    public partial class wording : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_activationCodes_users_UserId",
                table: "activationCodes");

            migrationBuilder.DropForeignKey(
                name: "FK_loginInformations_users_UserId",
                table: "loginInformations");

            migrationBuilder.DropPrimaryKey(
                name: "PK_users",
                table: "users");

            migrationBuilder.DropPrimaryKey(
                name: "PK_loginInformations",
                table: "loginInformations");

            migrationBuilder.DropPrimaryKey(
                name: "PK_activationCodes",
                table: "activationCodes");

            migrationBuilder.RenameTable(
                name: "users",
                newName: "Users");

            migrationBuilder.RenameTable(
                name: "loginInformations",
                newName: "LoginInformations");

            migrationBuilder.RenameTable(
                name: "activationCodes",
                newName: "ActivationCodes");

            migrationBuilder.RenameIndex(
                name: "IX_loginInformations_UserId",
                table: "LoginInformations",
                newName: "IX_LoginInformations_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_activationCodes_UserId",
                table: "ActivationCodes",
                newName: "IX_ActivationCodes_UserId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Users",
                table: "Users",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_LoginInformations",
                table: "LoginInformations",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_ActivationCodes",
                table: "ActivationCodes",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_ActivationCodes_Users_UserId",
                table: "ActivationCodes",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_LoginInformations_Users_UserId",
                table: "LoginInformations",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_ActivationCodes_Users_UserId",
                table: "ActivationCodes");

            migrationBuilder.DropForeignKey(
                name: "FK_LoginInformations_Users_UserId",
                table: "LoginInformations");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Users",
                table: "Users");

            migrationBuilder.DropPrimaryKey(
                name: "PK_LoginInformations",
                table: "LoginInformations");

            migrationBuilder.DropPrimaryKey(
                name: "PK_ActivationCodes",
                table: "ActivationCodes");

            migrationBuilder.RenameTable(
                name: "Users",
                newName: "users");

            migrationBuilder.RenameTable(
                name: "LoginInformations",
                newName: "loginInformations");

            migrationBuilder.RenameTable(
                name: "ActivationCodes",
                newName: "activationCodes");

            migrationBuilder.RenameIndex(
                name: "IX_LoginInformations_UserId",
                table: "loginInformations",
                newName: "IX_loginInformations_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_ActivationCodes_UserId",
                table: "activationCodes",
                newName: "IX_activationCodes_UserId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_users",
                table: "users",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_loginInformations",
                table: "loginInformations",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_activationCodes",
                table: "activationCodes",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_activationCodes_users_UserId",
                table: "activationCodes",
                column: "UserId",
                principalTable: "users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_loginInformations_users_UserId",
                table: "loginInformations",
                column: "UserId",
                principalTable: "users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
