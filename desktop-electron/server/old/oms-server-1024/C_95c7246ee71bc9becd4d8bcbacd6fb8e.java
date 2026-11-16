import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_95c7246ee71bc9becd4d8bcbacd6fb8e
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('dataadd.html?dataname=WORK_UNIT&&actiontype=edit&pk_id="+mapRow.get("ID")+"');";
return url;

    }
}
