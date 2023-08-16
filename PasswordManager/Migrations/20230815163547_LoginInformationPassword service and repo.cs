using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PasswordManager.Migrations
{
    public partial class LoginInformationPasswordserviceandrepo : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LoginInformationPassword_LoginInformation_LoginInformationId",
                table: "LoginInformationPassword");

            migrationBuilder.DropPrimaryKey(
                name: "PK_LoginInformationPassword",
                table: "LoginInformationPassword");

            migrationBuilder.RenameTable(
                name: "LoginInformationPassword",
                newName: "LoginInformationPasswords");

            migrationBuilder.RenameIndex(
                name: "IX_LoginInformationPassword_LoginInformationId",
                table: "LoginInformationPasswords",
                newName: "IX_LoginInformationPasswords_LoginInformationId");

            migrationBuilder.AddColumn<string>(
                name: "Domain",
                table: "LoginInformation",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddPrimaryKey(
                name: "PK_LoginInformationPasswords",
                table: "LoginInformationPasswords",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_LoginInformationPasswords_LoginInformation_LoginInformation~",
                table: "LoginInformationPasswords",
                column: "LoginInformationId",
                principalTable: "LoginInformation",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LoginInformationPasswords_LoginInformation_LoginInformation~",
                table: "LoginInformationPasswords");

            migrationBuilder.DropPrimaryKey(
                name: "PK_LoginInformationPasswords",
                table: "LoginInformationPasswords");

            migrationBuilder.DropColumn(
                name: "Domain",
                table: "LoginInformation");

            migrationBuilder.RenameTable(
                name: "LoginInformationPasswords",
                newName: "LoginInformationPassword");

            migrationBuilder.RenameIndex(
                name: "IX_LoginInformationPasswords_LoginInformationId",
                table: "LoginInformationPassword",
                newName: "IX_LoginInformationPassword_LoginInformationId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_LoginInformationPassword",
                table: "LoginInformationPassword",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_LoginInformationPassword_LoginInformation_LoginInformationId",
                table: "LoginInformationPassword",
                column: "LoginInformationId",
                principalTable: "LoginInformation",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
