using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace PasswordManager.Migrations
{
    public partial class UserPasswordTypo : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "DegreeOfParallism",
                table: "UserPasswords",
                newName: "DegreeOfParallelism");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "DegreeOfParallelism",
                table: "UserPasswords",
                newName: "DegreeOfParallism");
        }
    }
}
