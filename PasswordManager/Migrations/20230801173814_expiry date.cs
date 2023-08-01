using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PasswordManager.Migrations
{
    public partial class expirydate : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LoginInformations_Users_UserId",
                table: "LoginInformations");

            migrationBuilder.DropPrimaryKey(
                name: "PK_LoginInformations",
                table: "LoginInformations");

            migrationBuilder.RenameTable(
                name: "LoginInformations",
                newName: "LoginInformation");

            migrationBuilder.RenameIndex(
                name: "IX_LoginInformations_UserId",
                table: "LoginInformation",
                newName: "IX_LoginInformation_UserId");

            migrationBuilder.AddColumn<DateTime>(
                name: "ExpiryDate",
                table: "ActivationCodes",
                type: "timestamp with time zone",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddPrimaryKey(
                name: "PK_LoginInformation",
                table: "LoginInformation",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_LoginInformation_Users_UserId",
                table: "LoginInformation",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LoginInformation_Users_UserId",
                table: "LoginInformation");

            migrationBuilder.DropPrimaryKey(
                name: "PK_LoginInformation",
                table: "LoginInformation");

            migrationBuilder.DropColumn(
                name: "ExpiryDate",
                table: "ActivationCodes");

            migrationBuilder.RenameTable(
                name: "LoginInformation",
                newName: "LoginInformations");

            migrationBuilder.RenameIndex(
                name: "IX_LoginInformation_UserId",
                table: "LoginInformations",
                newName: "IX_LoginInformations_UserId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_LoginInformations",
                table: "LoginInformations",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_LoginInformations_Users_UserId",
                table: "LoginInformations",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
