import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_8d73a2cbfba28c2cc46d0663442bee3a
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('module/bussines/zwdd/index_eventlog_yx_edit_view.html?dataname="+mapRow.get("TABLE_NAME")+"&&actiontype=edit&pk_id="+mapRow.get("ID")+"');";
return url;

    }
}
