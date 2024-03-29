// define(['angular'], angular => {
    angular.module('num-2-phrase', [])
        .filter('num2phrase',function(){
            String.prototype.trimRight=function(){
                // убирает все пробелы в конце строки
                let r=/\s+$/g;
                return this.replace(r,'');
            }
            String.prototype.trimLeft=function(){
                // убирает все пробелы в начале строки
                let r=/^\s+/g;
                return this.replace(r,'');
            }
            String.prototype.trim=function(){
                // убирает все пробелы в начале и в конце строки
                return this.trimRight().trimLeft();
            }
            String.prototype.trimMiddle=function(){
                // убирает все пробелы в начале и в конце строки
                // помимо этого заменяет несколько подряд
                // идущих пробелов внутри строки на один пробел
                let r=/\s\s+/g;
                return this.trim().replace(r,' ');
            }
            String.prototype.trimAll=function(){
                // убирает все пробелы в строке s
                let r=/\s+/g;
                return this.replace(r,'');
            }
            String.prototype.repeat=function(n){
                // повторяет строку n раз
                let r='';
                if(typeof(n)=='number')
                    for(let i=1; i<=n; i++) r+=this;
                return r;
            }
            Number.prototype.isInt=function(){
                // возвращает True, если число является целым
                return (Math.round(this)==this);
            }
            Number.prototype.roundTo=function(n){
                // округляет число до заданного количества n
                // знаков после (или перед) запятой
                let x=0;
                if(typeof(n)=='number')
                    if(n.isInt())
                        if(n>= -6 && n<=6) x=n;
                x=Math.pow(10,x);
                return Math.round(this*x)/x;
            }

            // String.prototype.trimAll=function(){
            //     // убирает все пробелы в строке s
            //     let r=/\s+/g;
            //     return this.replace(r,'');
            // }

            Number.prototype.toPhrase=function(c){
                // сумма прописью для чисел от 0 до 999 триллионов
                // можно передать параметр "валюта": RUB,USD,EUR (по умолчанию RUB)
                let x=this.roundTo(2);
                if(x<0 || x>999999999999999.99) return false;
                let currency='RUB';
                if(typeof(c)=='string') currency=c.trimAll().toUpperCase();
                if(currency=='RUR') currency='RUB';
                if(currency!='RUB' && currency!='USD' && currency!='EUR') return false;
                let groups=new Array();
                groups[0]=new Array();
                groups[1]=new Array();
                groups[2]=new Array();
                groups[3]=new Array();
                groups[4]=new Array();
                groups[9]=new Array();
                // рубли
                // по умолчанию
                groups[0][-1]={'RUB':'рублей','USD':'долларов США','EUR':'евро'};
                //исключения
                groups[0][1]={'RUB':'рубль','USD':'доллар США','EUR':'евро'};
                groups[0][2]={'RUB':'рубля','USD':'доллара США','EUR':'евро'};
                groups[0][3]={'RUB':'рубля','USD':'доллара США','EUR':'евро'};
                groups[0][4]={'RUB':'рубля','USD':'доллара США','EUR':'евро'};
                // тысячи
                // по умолчанию
                groups[1][-1]='тысяч';
                //исключения
                groups[1][1]='тысяча';
                groups[1][2]='тысячи';
                groups[1][3]='тысячи';
                groups[1][4]='тысячи';
                // миллионы
                // по умолчанию
                groups[2][-1]='миллионов';
                //исключения
                groups[2][1]='миллион';
                groups[2][2]='миллиона';
                groups[2][3]='миллиона';
                groups[2][4]='миллиона';
                // миллиарды
                // по умолчанию
                groups[3][-1]='миллиардов';
                //исключения
                groups[3][1]='миллиард';
                groups[3][2]='миллиарда';
                groups[3][3]='миллиарда';
                groups[3][4]='миллиарда';
                // триллионы
                // по умолчанию
                groups[4][-1]='триллионов';
                //исключения
                groups[4][1]='триллион';
                groups[4][2]='триллиона';
                groups[4][3]='триллиона';
                groups[4][4]='триллиона';
                // копейки
                // по умолчанию
                groups[9][-1]={'RUB':'копеек','USD':'центов','EUR':'центов'};
                //исключения
                groups[9][1]={'RUB':'копейка','USD':'цент','EUR':'цент'};
                groups[9][2]={'RUB':'копейки','USD':'цента','EUR':'цента'};
                groups[9][3]={'RUB':'копейки','USD':'цента','EUR':'цента'};
                groups[9][4]={'RUB':'копейки','USD':'цента','EUR':'цента'};
                // цифры и числа
                // либо просто строка, либо 4 строки в хэше
                let names=new Array();
                names[1]={0:'один',1:'одна',2:'один',3:'один',4:'один'};
                names[2]={0:'два',1:'две',2:'два',3:'два',4:'два'};
                names[3]='три';
                names[4]='четыре';
                names[5]='пять';
                names[6]='шесть';
                names[7]='семь';
                names[8]='восемь';
                names[9]='девять';
                names[10]='десять';
                names[11]='одиннадцать';
                names[12]='двенадцать';
                names[13]='тринадцать';
                names[14]='четырнадцать';
                names[15]='пятнадцать';
                names[16]='шестнадцать';
                names[17]='семнадцать';
                names[18]='восемнадцать';
                names[19]='девятнадцать';
                names[20]='двадцать';
                names[30]='тридцать';
                names[40]='сорок';
                names[50]='пятьдесят';
                names[60]='шестьдесят';
                names[70]='семьдесят';
                names[80]='восемьдесят';
                names[90]='девяносто';
                names[100]='сто';
                names[200]='двести';
                names[300]='триста';
                names[400]='четыреста';
                names[500]='пятьсот';
                names[600]='шестьсот';
                names[700]='семьсот';
                names[800]='восемьсот';
                names[900]='девятьсот';
                let r='',i,j,y=Math.floor(x);
                // если НЕ ноль рублей
                if(y>0){
                    // выделим тройки с руб., тыс., миллионами, миллиардами и триллионами
                    let t=new Array();
                    for(i=0; i<=4; i++){
                        t[i]=y%1000;
                        y=Math.floor(y/1000);
                    }
                    let d=new Array();
                    // выделим в каждой тройке сотни, десятки и единицы
                    for(i=0; i<=4; i++){
                        d[i]=new Array();
                        d[i][0]=t[i]%10; // единицы
                        d[i][10]=t[i]%100-d[i][0]; // десятки
                        d[i][100]=t[i]-d[i][10]-d[i][0]; // сотни
                        d[i][11]=t[i]%100; // две правых цифры в виде числа
                    }
                    for(i=4; i>=0; i--)
                        if(t[i]>0){
                            if(names[d[i][100]]) r+=' '+((typeof(names[d[i][100]])=='object')?(names[d[i][100]][i]):(names[d[i][100]]));
                            if(names[d[i][11]]) r+=' '+((typeof(names[d[i][11]])=='object')?(names[d[i][11]][i]):(names[d[i][11]]));
                            else{
                                if(names[d[i][10]]) r+=' '+((typeof(names[d[i][10]])=='object')?(names[d[i][10]][i]):(names[d[i][10]]));
                                if(names[d[i][0]]) r+=' '+((typeof(names[d[i][0]])=='object')?(names[d[i][0]][i]):(names[d[i][0]]));
                            }
                            // если существует числительное
                            if(names[d[i][11]]) j=d[i][11];
                            else j=d[i][0];
                            if(groups[i][j])
                                if(i==0) r+=' '+groups[i][j][currency];
                                else r+=' '+groups[i][j];
                            else{
                                if(i==0) r+=' '+groups[i][-1][currency];
                                else r+=' '+groups[i][-1];
                            }
                        }
                    if(t[0]==0) r+=' '+groups[0][-1][currency];
                }else r='Ноль '+groups[0][-1][currency];
                y=((x-Math.floor(x))*100).roundTo();
                if(y<10) y='0'+y;
                r=r.trimMiddle();
                r=r.substr(0,1).toUpperCase()+r.substr(1);
                r+=' '+y;
                y=y*1;
                // если существует числительное
                if(names[y]) j=y;else j=y%10;
                if(groups[9][j]) r+=' '+groups[9][j][currency];
                else r+=' '+groups[9][-1][currency];
                return r;
            }
            return input =>  (new Number(input)).toPhrase('RUB');
        });
// });
