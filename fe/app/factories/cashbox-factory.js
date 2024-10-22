// /**
//  * Created 30.09.24.
//  */
angular.module('cashbox-factory', [])
    .factory('cashboxFactory',['paneFactory',
        function (paneFactory) {

          let moneyOperations = {
            moneyOrder : 'Внесение суммы',
            issuingCash : 'Изъятие наличных денежных средств'
          };
          let factoryName = 'cashboxFactory';
          let httpService = paneFactory.getHttpService();
          let cashboxInfo = {
          //Всегда: признак поддержки маркированных товаров
              Mark: false,
          //Признак открытой/закрытой кассовой смены
            ShiftOpened: false,
          //Количество не отправленных документов программной кассы
            DocsCount: 0,
          //Серийный номер СКО
            Serial: "",
          //Регистрационный номер ПК
            RegisterId: "",
//Модель и версия СКО
          Model: "",
//Наименование предприятия владельца ПК
          OrganizationName: "",

//УНП предприятия владельца ПК
          UNP: 0,

//Версия программной кассы
          Version: "",

//Реквизиты кассира, который был указан при открытии сеанса
          Cashier: "",

//Сумма наличных денег в кассе по основной валюте
          TotalSum: 0,
//Сумма наличных денег в кассе по всем валютам
          CashInAll: {
            "BYN": 0
          },
//Номер следующего кассового документа
          DocNumber: 0,
//Номер следующего сменного отчета
          ReportNumber: 0,
//Номер первого платежного документа в смене или 0, если смена не открытаRevocationServiceStatus": "String",
          FirstDocNumber: 0,
//Пустая строка или текст ошибки обновления СОС
          RevocationServiceStatus: "",
//Пустая строка или текст ошибки отправки документов
          DocumentsSenderStatus: "",
// Срок действия сертификата СКО (дата и время)
          CertExpDate: "",
//Состояние блокировки кассы АИС ККО или АИС ПКС
          IsBlocked: true,
//Текстовое описание причин блокировки кассы
          BlockedText: "",
//Базовый адрес для загрузки электронных чеков
          ElCheckDefaultUrl: "",
//Возраст самого старого документа в СКО, в сутках с двумя десятичными
//             разрядами после запятой.
          OldestDocumentAge: 0,
          TitanKey:""
        };

        let cashboxUpdate = ($s) => {
          httpService.posttp(
            { url: 'info',
              data : {'TitanKey' : cashboxInfo.TitanKey},
              requestParams:$s.requestParams}).then(
            resp => {
              $s.cashboxInfo = cashboxInfo = resp;
              $s.warning = "info_success";
              // console.log(resp);
            },
            resp => {
              $s.warning = "info_failed ";
            });

        };

          let open = ($s) => {
            httpService.posttp(
              { url: 'open',
                data : {
                  'TitanKey' : cashboxInfo.TitanKey,
                  'Pin': "123456",
                  'Cashier': paneFactory.user.name
                },
                requestParams:$s.requestParams}).then(
              resp => {
                cashboxInfo.TitanKey = resp.TitanKey;
                $s.warning = "open_success";
              },
              resp => { $s.warning = "open_failed "; });
          };

          let close = ($s) => {
            httpService.posttp(
              { url: 'close',
                data : { 'TitanKey' : cashboxInfo.TitanKey },
                requestParams:$s.requestParams}).then(
              resp => {
                cashboxInfo.TitanKey = resp.TitanKey;
                $s.warning = "close_success";
              },
              resp => { $s.warning = "close_failed "; });
          };


          let init = ($s) => {
            httpService.posttp({ url: 'init', requestParams:$s.requestParams}).then(
              resp => {
                cashboxInfo.TitanKey = resp.TitanKey;
                $s.warning = "init_success";
                // console.log(resp);
              },
              resp => {
                $s.warning = "init_failed ";
              });
        };

        let info = ($s) => {};

        return {
          factoryName,
          moneyOperations,
          cashboxInfo,
          setMethods: ($s) => {
            $s.cashboxUpdate = () => {
              return cashboxUpdate($s);
            };
            $s.openCashbox = () => {return open($s)};
            $s.closeCashbox = () => {return close($s)};
            $s.initCashbox = () => {return init($s)};
            $s.cashboxUpdate = () => {return cashboxUpdate($s)};
          },
          getTotals: () => {}
            };
        }
    ]);