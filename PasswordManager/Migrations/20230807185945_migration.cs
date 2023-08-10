using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PasswordManager.Migrations
{
    public partial class migration : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_UserPassword_Users_UserId",
                table: "UserPassword");

            migrationBuilder.DropPrimaryKey(
                name: "PK_UserPassword",
                table: "UserPassword");

            migrationBuilder.RenameTable(
                name: "UserPassword",
                newName: "UserPasswords");

            migrationBuilder.RenameIndex(
                name: "IX_UserPassword_UserId",
                table: "UserPasswords",
                newName: "IX_UserPasswords_UserId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_UserPasswords",
                table: "UserPasswords",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_UserPasswords_Users_UserId",
                table: "UserPasswords",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_UserPasswords_Users_UserId",
                table: "UserPasswords");

            migrationBuilder.DropPrimaryKey(
                name: "PK_UserPasswords",
                table: "UserPasswords");

            migrationBuilder.RenameTable(
                name: "UserPasswords",
                newName: "UserPassword");

            migrationBuilder.RenameIndex(
                name: "IX_UserPasswords_UserId",
                table: "UserPassword",
                newName: "IX_UserPassword_UserId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_UserPassword",
                table: "UserPassword",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_UserPassword_Users_UserId",
                table: "UserPassword",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
