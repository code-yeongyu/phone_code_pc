import sys


def main():
    if len(sys.argv) > 1 :
        try :
            funcs = str(dir(__import__(sys.argv[1])))
        except ModuleNotFoundError:
            print("You don't have that module.")
            return;
        funcs = funcs.replace("\'", '\"').replace(',', ',\n')
        funcs = '{"'+sys.argv[1]+'": '+funcs+"}"
        file = open("function_lists_"+sys.argv[1]+".json", 'w+')
        file.write(funcs)
        file.close()
    else :
        print("Type the module that you want to get the functions list by an argument.")

main()
